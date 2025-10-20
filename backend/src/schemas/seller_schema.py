from datetime import datetime
from typing import Annotated

from pydantic import BaseModel, EmailStr, Field, field_validator

from src.errors.errors import BadRequestException
from src.schemas.zone_schema import ZoneBase


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
    created_at: Annotated[datetime | None, Field()] = None
    zone: Annotated[ZoneBase | None, Field()] = None

    @field_validator("phone")
    def validate_phone(cls, value: str) -> str:
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise BadRequestException("Phone must be between 9 and 15 digits")
        return value

    model_config = {"str_strip_whitespace": True, "from_attributes": True}


class SellerCreateRequest(SellerBase):
    zone_id: Annotated[str, Field(min_length=36, max_length=36)]


class SellerCreateResponse(SellerBase):
    pass


class GetSellerResponse(SellerBase):
    pass


class GetSellersResponse(BaseModel):
    total_count: int
    sellers: list[SellerBase]
