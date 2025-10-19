from datetime import datetime
from typing import Annotated

from pydantic import BaseModel, Field

from src.schemas.product_schema import ProductBase
from src.schemas.seller_schema import SellerBase
from src.schemas.zone_schema import ZoneBase


class SellingPlanBase(BaseModel):
    id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    period: Annotated[str, Field(min_length=1, max_length=6)]
    goal: Annotated[int, Field(gt=0)]
    created_at: Annotated[datetime | None, Field()] = None
    product: Annotated[ProductBase | None, Field()] = None
    zone: Annotated[ZoneBase | None, Field()] = None
    seller: Annotated[SellerBase | None, Field()] = None

    model_config = {"str_strip_whitespace": True, "from_attributes": True}


class SellingPlanCreateRequest(SellingPlanBase):
    product_id: Annotated[str, Field(min_length=36, max_length=36)]
    zone_id: Annotated[str, Field(min_length=36, max_length=36)]
    seller_id: Annotated[str, Field(min_length=36, max_length=36)]


class SellingPlanCreateResponse(SellingPlanBase):
    pass


class GetSellingPlanResponse(SellingPlanBase):
    pass


class GetSellingPlansResponse(BaseModel):
    total_count: int
    selling_plans: list[SellingPlanBase]
