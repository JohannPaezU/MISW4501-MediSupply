from typing import Annotated

from pydantic import BaseModel, Field


class ZoneBase(BaseModel):
    id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    description: Annotated[str, Field(min_length=1, max_length=255)]

    model_config = {"str_strip_whitespace": True, "from_attributes": True}


class GetZonesResponse(BaseModel):
    total_count: int
    zones: list[ZoneBase]
