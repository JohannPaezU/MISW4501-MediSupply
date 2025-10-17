from datetime import datetime
from typing import Annotated
from pydantic import BaseModel, EmailStr, Field, field_validator, model_validator
from src.errors.errors import BadRequestException


class SellerBase(BaseModel):
    id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    full_name: Annotated[str, Field(min_length=1, max_length=100)]
    doi: Annotated[
        str,
        Field(
            min_length=1,
            max_length=50,
            description="Unique user identification number (NIT, RUC, ID, etc.)",
        ),
    ]
    email: Annotated[EmailStr, Field(min_length=5, max_length=120)]
    phone: Annotated[str, Field(min_length=9, max_length=15)]
    zone_id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    zone_description: Annotated[str | None, Field(min_length=1, max_length=255)] = None
    created_at: Annotated[datetime | None, Field()] = None

    @field_validator("phone")
    def validate_phone(cls, value: str) -> str:
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise BadRequestException("Phone must be between 9 and 15 digits")
        return value

    @model_validator(mode="before")
    def extract_zone(cls, values):
        if hasattr(values, "zone") and values.zone:
            values.zone_id = values.zone.id
            values.zone_description = values.zone.description
        return values

    model_config = {"str_strip_whitespace": True, "from_attributes": True}


class SellerCreateRequest(SellerBase):
    zone_id: Annotated[str, Field(min_length=36, max_length=36)]


class SellerCreateResponse(SellerBase): ...


class GetSellersResponse(BaseModel):
    total_count: int
    sellers: list[SellerBase]
