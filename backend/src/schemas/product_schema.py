from datetime import date, datetime
from typing import Annotated

from pydantic import Field

from src.models.db_models import OrderProduct
from src.models.enums.order_status import OrderStatus
from src.schemas.base_schema import (
    BaseSchema,
    ProductBase,
    ProviderBase,
    SellingPlanBase,
)


class ProductCreateRequest(BaseSchema):
    name: Annotated[str, Field(min_length=3, max_length=100)]
    details: Annotated[str, Field(min_length=10, max_length=255)]
    store: Annotated[str, Field(min_length=3, max_length=100)]
    batch: Annotated[str, Field(min_length=5, max_length=50)]
    image_url: Annotated[str | None, Field(min_length=10, max_length=255)] = None
    due_date: Annotated[date, Field()]
    stock: Annotated[int, Field(gt=0)]
    price_per_unit: Annotated[float, Field(gt=0)]
    provider_id: Annotated[str, Field(min_length=36, max_length=36)]


class OrderProductDetail(BaseSchema):
    id: str
    delivery_date: date
    status: OrderStatus
    quantity: int
    created_at: datetime

    @classmethod
    def from_order_product(cls, order_product: OrderProduct) -> "OrderProductDetail":
        return cls(
            id=order_product.order.id,
            delivery_date=order_product.order.delivery_date,
            status=order_product.order.status,
            quantity=order_product.quantity,
            created_at=order_product.order.created_at,
        )


class ProductResponse(ProductBase):
    provider: ProviderBase
    selling_plans: list[SellingPlanBase]
    orders: list[OrderProductDetail]


class ProductCreateBulkRequest(BaseSchema):  # noqa
    products: Annotated[list[ProductCreateRequest], Field(min_length=1)]


class ProductCreateBulkResponse(BaseSchema):
    success: bool
    rows_total: int
    rows_inserted: int
    errors: int
    errors_details: list[str]


class GetProductsResponse(BaseSchema):
    total_count: int
    products: list[ProductBase]
