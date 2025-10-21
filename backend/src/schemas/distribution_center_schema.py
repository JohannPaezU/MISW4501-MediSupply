from datetime import datetime
from typing import Annotated

from pydantic import Field

from src.schemas.base_schema import BaseSchema


class DistributionCenterBase(BaseSchema):
    id: Annotated[str, Field(min_length=36, max_length=36)]
    name: Annotated[str, Field(min_length=1, max_length=100)]
    address: Annotated[str, Field(min_length=1, max_length=255)]
    city: Annotated[str, Field(min_length=1, max_length=100)]
    country: Annotated[str, Field(min_length=1, max_length=100)]
    created_at: Annotated[datetime, Field()]
    # orders: Annotated[list[OrderBase], Field()]


class GetDistributionCentersResponse(BaseSchema):
    total_count: int
    distribution_centers: list[DistributionCenterBase]
