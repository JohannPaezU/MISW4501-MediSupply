from typing import Annotated

from pydantic import EmailStr, Field, field_validator

from src.errors.errors import BadRequestException
from src.schemas.base_schema import (
    BaseSchema,
    OrderBase,
    SellerBase,
    SellingPlanBase,
    UserBase,
    ZoneBase,
)


class SellerCreateRequest(BaseSchema):
    full_name: Annotated[str, Field(min_length=1, max_length=100)]
    email: Annotated[EmailStr, Field(min_length=5, max_length=120)]
    phone: Annotated[str, Field(min_length=9, max_length=15)]
    doi: Annotated[
        str,
        Field(
            min_length=1,
            max_length=50,
            description="Unique user identification number (NIT, RUC, ID, etc.)",
        ),
    ]
    address: Annotated[str | None, Field(min_length=1, max_length=255)] = None
    zone_id: Annotated[str, Field(min_length=36, max_length=36)]

    @field_validator("phone")
    def validate_phone(cls, value: str) -> str:
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise BadRequestException("Phone must be between 9 and 15 digits")
        return value


class SellerResponse(SellerBase):
    zone: ZoneBase
    clients: list[UserBase]
    selling_plans: list[SellingPlanBase]
    managed_orders: list[OrderBase]


class GetSellersResponse(BaseSchema):
    total_count: int
    sellers: list[SellerBase]


class GetClientsResponse(BaseSchema):
    total_count: int
    clients: list[UserBase]
