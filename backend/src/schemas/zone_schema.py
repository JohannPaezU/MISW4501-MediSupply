from typing import Annotated

from pydantic import Field

from src.schemas.base_schema import BaseSchema


class ZoneBase(BaseSchema):
    id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    description: Annotated[str, Field(min_length=1, max_length=255)]


class GetZonesResponse(BaseSchema):
    total_count: int
    zones: list[ZoneBase]
