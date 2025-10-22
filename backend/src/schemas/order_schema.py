from pydantic import Field
from typing import List, Optional, Annotated
from datetime import date
from src.schemas.base_schema import BaseSchema, OrderBase, SellerBase, UserBase, DistributionCenterBase, \
    OrderProductBase


class OrderProductCreateRequest(BaseSchema):
    product_id: Annotated[str, Field(min_length=36, max_length=36)]
    quantity: Annotated[int, Field(gt=0)]


class OrderCreateRequest(BaseSchema):
    comments: Optional[Annotated[str | None, Field(max_length=255)]] = None
    delivery_date: Annotated[date, Field()]
    distribution_center_id: Annotated[str, Field(min_length=36, max_length=36)]
    products: List[OrderProductCreateRequest]


class OrderCreateBySellerRequest(OrderCreateRequest):
    client_id: Annotated[str, Field(min_length=36, max_length=36)]


class OrderCreateByClientRequest(OrderCreateRequest):
    pass


class OrderResponse(OrderBase):
    seller: SellerBase | None = None
    client: UserBase
    distribution_center: DistributionCenterBase
    order_products: list[OrderProductBase]


class GetOrdersResponse(BaseSchema):
    total_count: int
    orders: list[OrderBase]
