from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.errors.errors import UnprocessableEntityException
from src.models.db_models import Product
from src.schemas.product_schema import (
    ProductCreateBulkRequest,
    ProductCreateBulkResponse,
    ProductCreateRequest,
)
from src.services.provider_service import get_provider_by_id


def create_product(
    *, db: Session, product_create_request: ProductCreateRequest
) -> Product:  # pragma: no cover
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
        price_per_unite=product_create_request.price_per_unite,
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
) -> ProductCreateBulkResponse:  # pragma: no cover
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


def get_products(*, db: Session) -> list[Product]:  # pragma: no cover
    return db.query(Product).order_by(Product.name).all()  # type: ignore


def get_product_by_id(
    *, db: Session, product_id: str
) -> Product | None:  # pragma: no cover
    return db.query(Product).filter_by(id=product_id).first()
