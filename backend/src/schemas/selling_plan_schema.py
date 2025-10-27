from typing import Annotated

from pydantic import Field

from src.schemas.base_schema import (
    BaseSchema,
    ProductBase,
    SellerBase,
    SellingPlanBase,
    ZoneBase,
)


class SellingPlanCreateRequest(BaseSchema):
    period: Annotated[str, Field(min_length=1, max_length=20)]
    goal: Annotated[int, Field(gt=0)]
    product_id: Annotated[str, Field(min_length=36, max_length=36)]
    zone_id: Annotated[str, Field(min_length=36, max_length=36)]
    seller_id: Annotated[str, Field(min_length=36, max_length=36)]


class SellingPlanResponse(SellingPlanBase):
    product: ProductBase
    zone: ZoneBase | None = None
    seller: SellerBase | None = None


class GetSellingPlansResponse(BaseSchema):
    total_count: int
    selling_plans: list[SellingPlanResponse]
