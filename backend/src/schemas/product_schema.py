from datetime import date, datetime
from typing import Annotated

from pydantic import Field

from src.schemas.base_schema import BaseSchema
from src.schemas.provider_schema import ProviderBase


class ProductBase(BaseSchema):
    id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    name: Annotated[str, Field(min_length=3, max_length=100)]
    details: Annotated[str, Field(min_length=10, max_length=255)]
    store: Annotated[str, Field(min_length=3, max_length=100)]
    batch: Annotated[str, Field(min_length=5, max_length=50)]
    image_url: Annotated[str | None, Field(min_length=10, max_length=255)] = None
    due_date: Annotated[date, Field()]
    stock: Annotated[int, Field(gt=0)]
    price_per_unite: Annotated[float, Field(gt=0)]
    created_at: Annotated[datetime | None, Field()] = None
    provider: Annotated[ProviderBase | None, Field()] = None


class ProductCreateRequest(ProductBase):
    provider_id: Annotated[str, Field(min_length=36, max_length=36)]


class ProductCreateResponse(ProductBase):
    pass


class ProductCreateBulkRequest(BaseSchema):
    products: list[ProductCreateRequest]


class ProductCreateBulkResponse(BaseSchema):
    success: bool
    rows_total: int
    rows_inserted: int
    errors: int
    errors_details: list[str]


class GetProductResponse(ProductBase):
    pass


class GetProductsResponse(BaseSchema):
    total_count: int
    products: list[ProductBase]
