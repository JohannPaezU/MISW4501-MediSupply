from sqlalchemy import desc, func
from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.errors.errors import NotFoundException, UnprocessableEntityException
from src.models.db_models import Order, OrderProduct, Product, User
from src.models.enums.user_role import UserRole
from src.schemas.product_schema import (
    ProductCreateBulkRequest,
    ProductCreateBulkResponse,
    ProductCreateRequest,
)
from src.services.provider_service import get_provider_by_id
from src.services.seller_service import get_institutional_client_for_seller
from src.services.user_service import get_user_by_id


def create_product(
    *, db: Session, product_create_request: ProductCreateRequest
) -> Product:
    existing_provider = get_provider_by_id(
        db=db, provider_id=product_create_request.provider_id
    )
    if not existing_provider:
        raise UnprocessableEntityException("Provider with the given ID does not exist")

    product = Product(
        name=product_create_request.name,
        details=product_create_request.details,
        store=product_create_request.store,
        batch=product_create_request.batch,
        image_url=product_create_request.image_url,
        due_date=product_create_request.due_date,
        stock=product_create_request.stock,
        price_per_unit=product_create_request.price_per_unit,
        provider_id=product_create_request.provider_id,
    )

    db.add(product)
    db.commit()
    db.refresh(product)
    logger.info(
        f"Product created successfully with id [{product.id}] and name [{product.name}]"
    )

    return product


def create_products_bulk(
    *, db: Session, product_create_bulk_request: ProductCreateBulkRequest
) -> ProductCreateBulkResponse:
    rows_total = len(product_create_bulk_request.products)
    rows_inserted = 0
    errors_details = []

    for product in product_create_bulk_request.products:
        try:
            create_product(db=db, product_create_request=product)
            rows_inserted += 1
        except UnprocessableEntityException as e:
            errors_details.append(f"Error for product '{product.name}': {str(e)}")
        except Exception as e:
            errors_details.append(
                f"Unexpected error for product '{product.name}': {str(e)}"
            )

    errors = rows_total - rows_inserted

    return ProductCreateBulkResponse(
        success=errors == 0,
        rows_total=rows_total,
        rows_inserted=rows_inserted,
        errors=errors,
        errors_details=errors_details,
    )


def get_products(
    *, db: Session, current_user: User, limit: int | None = None
) -> list[Product]:
    query = db.query(Product)
    if current_user.role != UserRole.ADMIN:
        query = query.filter(Product.stock > 0)
    query = query.order_by(Product.name)
    if limit:
        query = query.limit(limit)

    return query.all()  # type: ignore


def get_product_by_id(*, db: Session, product_id: str) -> Product | None:
    return db.query(Product).filter_by(id=product_id).first()


def _get_ranked_products(
    *, db: Session, limit: int, client_id: str | None = None
) -> list[Product]:
    query = (
        db.query(
            Product,
            func.sum(OrderProduct.quantity).label("total_quantity"),
            func.max(Order.created_at).label("last_purchase_date"),
        )
        .join(OrderProduct, Product.id == OrderProduct.product_id)
        .join(Order, Order.id == OrderProduct.order_id)
        .filter(Product.stock > 0)
        .group_by(Product.id)
        .order_by(desc("total_quantity"), desc("last_purchase_date"))
    )
    if client_id:
        query = query.filter(Order.client_id == client_id)
    ranked_products = query.limit(limit).all()

    return [row[0] for row in ranked_products]


def get_recommended_products(
    *, db: Session, current_user: User, client_id: str, limit: int
) -> list[Product]:
    client = (
        get_institutional_client_for_seller(
            db=db, seller_id=current_user.id, client_id=client_id
        )
        if current_user.role == UserRole.COMMERCIAL
        else get_user_by_id(db=db, user_id=client_id)
    )
    if not client:
        raise NotFoundException("Client not found")
    products = _get_ranked_products(db=db, limit=limit, client_id=client_id)
    if not products:
        products = _get_ranked_products(db=db, limit=limit)
    if not products:
        products = get_products(db=db, current_user=current_user, limit=limit)

    return products
