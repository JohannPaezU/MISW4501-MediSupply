from datetime import date
from typing import Annotated

from pydantic import Field

from src.schemas.base_schema import BaseSchema, ProductBase, ProviderBase, SellingPlanBase, OrderProductBase


class ProductCreateRequest(BaseSchema):
    name: Annotated[str, Field(min_length=3, max_length=100)]
    details: Annotated[str, Field(min_length=10, max_length=255)]
    store: Annotated[str, Field(min_length=3, max_length=100)]
    batch: Annotated[str, Field(min_length=5, max_length=50)]
    image_url: Annotated[str | None, Field(min_length=10, max_length=255)] = None
    due_date: Annotated[date, Field()]
    stock: Annotated[int, Field(gt=0)]
    price_per_unite: Annotated[float, Field(gt=0)]
    provider_id: Annotated[str, Field(min_length=36, max_length=36)]


class ProductResponse(ProductBase):
    provider: ProviderBase
    selling_plans: list[SellingPlanBase]
    order_products: list[OrderProductBase]


class ProductCreateBulkRequest(BaseSchema): # noqa
    products: list[ProductCreateRequest]


class ProductCreateBulkResponse(BaseSchema):
    success: bool
    rows_total: int
    rows_inserted: int
    errors: int
    errors_details: list[str]


class GetProductsResponse(BaseSchema):
    total_count: int
    products: list[ProductBase]
