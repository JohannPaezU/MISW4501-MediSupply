from datetime import datetime

from sqlalchemy.orm import Session


from src.models.db_models import Order
from src.models.enums.order_status import OrderStatus


def get_orders_report(
    *,
    db: Session,
    seller_id: str | None = None,
    order_status: OrderStatus | None = None,
    start_date: datetime | None,
    end_date: datetime | None
) -> list[Order]:
    query = db.query(Order).filter(Order.seller_id.isnot(None))
    if seller_id:
        query = query.filter_by(seller_id=seller_id)
    if order_status:
        query = query.filter_by(status=order_status.name)
    if start_date:
        query = query.filter(Order.created_at >= start_date)
    if end_date:
        query = query.filter(Order.created_at <= end_date)

    return query.all()  # type: ignore
