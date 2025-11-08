from datetime import date

from fastapi import APIRouter, Depends, status, Query
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import BadRequestException, NotFoundException
from src.models.db_models import Order, User
from src.models.enums.order_status import OrderStatus
from src.models.enums.user_role import UserRole
from src.schemas.order_schema import (
    GetOrdersResponse,
    OrderCreateRequest,
    OrderProductDetail,
    OrderResponse,
)
from src.services.order_service import create_order, get_order_by_id, get_orders

order_router = APIRouter(tags=["Orders"], prefix="/orders")


@order_router.post(
    "",
    response_model=OrderResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register a new order",
    description="""
Create a new order in the system.

### Request Body
- **comments**: Optional comments about the order (max 255 characters).
- **delivery_date**: Date when the order should be delivered.
- **distribution_center_id**: ID of the distribution center (36 characters).
- **client_id**: (Commercial users only) ID of the client for whom the order is created (36 characters).
- **products**: List of products to be included in the order, each with:
    - **product_id**: ID of the product (36 characters).
    - **quantity**: Quantity of the product (must be greater than 0).

### Response
- **id**: Unique identifier of the order.
- **comments**: Comments about the order.
- **delivery_date**: Date when the order should be delivered.
- **status**: Current status of the order.
- **created_at**: Timestamp when the order was created.
- **seller**: Information about the seller who created the order (if applicable).
- **client**: Information about the client for whom the order was created.
- **distribution_center**: Information about the distribution center.
- **products**: List of products included in the order with their details.
""",
)
async def register_order(
    *,
    order_create_request: OrderCreateRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(allowed_roles=[UserRole.COMMERCIAL, UserRole.INSTITUTIONAL])
    ),
) -> OrderResponse:
    if current_user.role == UserRole.COMMERCIAL:
        if not order_create_request.client_id:
            raise BadRequestException("Client ID must be provided for commercial users")
        order = create_order(
            db=db,
            order_create_request=order_create_request,
            client_id=order_create_request.client_id,
            seller_id=current_user.id,
        )
    else:
        order = create_order(
            db=db, order_create_request=order_create_request, client_id=current_user.id
        )

    return _build_order_response(order=order)


@order_router.get(
    "",
    response_model=GetOrdersResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all orders created by the current user",
    description="""
Retrieve a list of all orders created by the current user.

### Query Parameters
- **delivery_date**: (Optional) Filter orders by delivery date (YYYY-MM-DD).
- **order_status**: (Optional) Filter orders by their status. Possible values:
    - received
    - preparing
    - in_transit
    - delivered
    - returned

### Response
- **total_count**: Total number of orders created by the user.
- **orders**: List of orders with their basic information:
    - **id**: Unique identifier of the order.
    - **comments**: Comments about the order.
    - **delivery_date**: Date when the order should be delivered.
    - **status**: Current status of the order.
    - **created_at**: Timestamp when the order was created.
""",
)
async def get_all_orders(
    *,
    delivery_date: date | None = Query(None),
    order_status: OrderStatus | None = Query(None),
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(allowed_roles=[UserRole.ADMIN, UserRole.COMMERCIAL, UserRole.INSTITUTIONAL])
    ),
) -> GetOrdersResponse:
    orders = get_orders(db=db, current_user=current_user, delivery_date=delivery_date, order_status=order_status)

    return GetOrdersResponse(total_count=len(orders), orders=orders)


@order_router.get(
    "/{order_id}",
    response_model=OrderResponse,
    status_code=status.HTTP_200_OK,
    summary="Get order by ID for the current user",
    description="""
Retrieve detailed information about a specific order by its ID for the current user.

### Path Parameters
- **order_id**: Unique identifier of the order (36 characters).

### Response
- **id**: Unique identifier of the order.
- **comments**: Comments about the order.
- **delivery_date**: Date when the order should be delivered.
- **status**: Current status of the order.
- **created_at**: Timestamp when the order was created.
- **seller**: Information about the seller who created the order (if applicable).
- **client**: Information about the client for whom the order was created.
- **distribution_center**: Information about the distribution center.
- **products**: List of products included in the order with their details.
""",
)
async def get_order(
    *,
    order_id: str,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(allowed_roles=[UserRole.ADMIN, UserRole.COMMERCIAL, UserRole.INSTITUTIONAL])
    ),
) -> OrderResponse:
    order = get_order_by_id(db=db, current_user=current_user, order_id=order_id)
    if not order:
        raise NotFoundException("Order not found")

    return _build_order_response(order=order)


def _build_order_response(order: Order) -> OrderResponse:
    return OrderResponse(
        **order.__dict__,
        seller=order.seller,
        client=order.client,
        distribution_center=order.distribution_center,
        products=[
            OrderProductDetail.from_order_product(order_product=order_product)
            for order_product in order.order_products
        ],
    )
