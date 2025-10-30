from datetime import datetime

from fastapi import APIRouter, Depends, Query, status
from sqlalchemy.orm import Session

from models.enums.order_status import OrderStatus
from schemas.order_schema import OrderProductDetail
from schemas.report_schema import GetOrderReportResponse, OrderReportResponse
from schemas.seller_schema import SellerMinimalResponse
from services.report_service import get_orders_report
from src.core.security import require_roles
from src.db.database import get_db
from src.models.db_models import Order
from src.models.enums.user_role import UserRole

report_router = APIRouter(
    tags=["Reports"],
    prefix="/reports",
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
)


@report_router.get(
    "/orders",
    response_model=GetOrderReportResponse,
    status_code=status.HTTP_200_OK,
    summary="Generate Orders Report",
    description="""
Generate a report of orders with optional filters such as seller ID, order status, and date range.

### Query Parameters:
- **seller_id**: (Optional) Filter orders by the seller's unique identifier.
- **order_status**: (Optional) Filter orders by their status. Possible values are: `received`, `preparing`, `in_transit`, `delivered`, `returned`.
- **start_date**: (Optional) Filter orders created on or after this date.
- **end_date**: (Optional) Filter orders created on or before this date.

### Response:
- **total_count**: Total number of orders matching the filters.
- **orders**: List of orders with the following details:
    - **id**: Unique identifier of the order.
    - **comments**: Any comments associated with the order.
    - **delivery_date**: Scheduled delivery date of the order.
    - **status**: Current status of the order.
    - **created_at**: Timestamp when the order was created.
    - **seller**: Basic information about the seller associated with the order.
    - **products**: List of products in the order with their details.
""",
)
async def get_all_orders_report(
    *,
    db: Session = Depends(get_db),
    seller_id: str | None = Query(None),
    order_status: OrderStatus | None = Query(None),
    start_date: datetime | None = Query(None),
    end_date: datetime | None = Query(None),
) -> GetOrderReportResponse:
    orders = get_orders_report(
        db=db,
        seller_id=seller_id,
        order_status=order_status,
        start_date=start_date,
        end_date=end_date,
    )
    orders = [_build_order_report_response(order=order) for order in orders]

    return GetOrderReportResponse(total_count=len(orders), orders=orders)


def _build_order_report_response(order: Order) -> OrderReportResponse:
    return OrderReportResponse(
        **order.__dict__,
        seller=SellerMinimalResponse.model_validate(order.seller),
        products=[
            OrderProductDetail.from_order_product(order_product=order_product)
            for order_product in order.order_products
        ],
    )
