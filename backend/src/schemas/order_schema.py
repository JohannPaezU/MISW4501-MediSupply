from datetime import date
from typing import Annotated

from pydantic import Field

from src.models.db_models import OrderProduct
from src.schemas.base_schema import (
    BaseSchema,
    DistributionCenterBase,
    OrderBase,
    RouteBase,
    SellerBase,
    UserBase,
)


class OrderProductCreateRequest(BaseSchema):
    product_id: Annotated[str, Field(min_length=36, max_length=36)]
    quantity: Annotated[int, Field(gt=0)]


class OrderCreateRequest(BaseSchema):
    comments: Annotated[str | None, Field(max_length=255)] = None
    delivery_date: Annotated[date, Field()]
    distribution_center_id: Annotated[str, Field(min_length=36, max_length=36)]
    client_id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    products: Annotated[list[OrderProductCreateRequest], Field(min_length=1)]


class OrderProductDetail(BaseSchema):
    id: str
    name: str
    store: str
    batch: str
    due_date: date
    price_per_unit: float
    quantity: int
    image_url: str

    @classmethod
    def from_order_product(cls, order_product: OrderProduct) -> "OrderProductDetail":
        return cls(
            id=order_product.product.id,
            name=order_product.product.name,
            store=order_product.product.store,
            batch=order_product.product.batch,
            due_date=order_product.product.due_date,
            price_per_unit=order_product.product.price_per_unit,
            quantity=order_product.quantity,
            image_url=order_product.product.image_url,
        )


class OrderResponse(OrderBase):
    seller: SellerBase | None = None
    client: UserBase
    distribution_center: DistributionCenterBase
    route: RouteBase | None = None
    products: list[OrderProductDetail]


class OrderMinimalResponse(OrderBase):
    distribution_center: DistributionCenterBase
    route: RouteBase | None = None


class GetOrdersResponse(BaseSchema):
    total_count: int
    orders: list[OrderMinimalResponse]
