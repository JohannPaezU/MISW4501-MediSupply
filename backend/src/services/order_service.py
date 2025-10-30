from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.errors.errors import (
    ApiError,
    BadRequestException,
    ConflictException,
    NotFoundException,
)
from src.models.db_models import Order, OrderProduct, User
from src.models.enums.user_role import UserRole
from src.schemas.order_schema import OrderCreateRequest
from src.services.distribution_center_service import get_distribution_center_by_id
from src.services.product_service import get_product_by_id
from src.services.seller_service import get_institutional_client_for_seller
from src.services.user_service import get_user_by_id


def create_order(
    *,
    db: Session,
    order_create_request: OrderCreateRequest,
    client_id: str,
    seller_id: str | None = None,
) -> Order:
    _validate_order_request(
        db=db,
        order_create_request=order_create_request,
        client_id=client_id,
        seller_id=seller_id,
    )
    order = Order(
        comments=order_create_request.comments,
        delivery_date=order_create_request.delivery_date,
        seller_id=seller_id,
        client_id=client_id,
        distribution_center_id=order_create_request.distribution_center_id,
    )

    db.add(order)
    db.flush()

    for item in order_create_request.products:
        product = get_product_by_id(db=db, product_id=item.product_id)
        if not product:
            raise NotFoundException(f"Product '{item.product_id}' not found")

        if product.stock < item.quantity:
            raise ConflictException(
                f"Insufficient stock for product '{product.name}'. \
                Available: {product.stock}, requested: {item.quantity}",
            )

        product.stock -= item.quantity

        order_product = OrderProduct(
            quantity=item.quantity,
            order_id=order.id,
            product_id=product.id,
        )
        db.add(order_product)

    try:
        db.commit()
    except Exception as e:
        logger.error(f"Error creating order: {str(e)}")
        raise ApiError("An error occurred while creating the order")

    db.refresh(order)
    return order


def get_orders(*, db: Session, current_user: User) -> list[Order]:
    query = db.query(Order)
    if current_user.role == UserRole.COMMERCIAL:
        query = query.filter_by(seller_id=current_user.id)
    else:
        query = query.filter_by(client_id=current_user.id)

    return query.all()  # type: ignore


def get_order_by_id(*, db: Session, current_user: User, order_id: str) -> Order | None:
    query = db.query(Order).filter_by(id=order_id)
    if current_user.role == UserRole.COMMERCIAL:
        query = query.filter_by(seller_id=current_user.id)
    else:
        query = query.filter_by(client_id=current_user.id)

    return query.first()


def _validate_order_request(
    *,
    db: Session,
    order_create_request: OrderCreateRequest,
    client_id: str,
    seller_id: str | None = None,
) -> None:
    product_ids = [item.product_id for item in order_create_request.products]
    duplicated_ids = set(x for x in product_ids if product_ids.count(x) > 1)
    if duplicated_ids:
        raise BadRequestException(
            f"Duplicated product IDs in order: {', '.join(duplicated_ids)}"
        )

    distribution_center = get_distribution_center_by_id(
        db=db, distribution_center_id=order_create_request.distribution_center_id
    )
    client = (
        get_institutional_client_for_seller(
            db=db, seller_id=seller_id, client_id=client_id
        )
        if seller_id
        else get_user_by_id(db=db, user_id=client_id)
    )

    if not distribution_center or not client:
        raise NotFoundException("Distribution center or client not found")
