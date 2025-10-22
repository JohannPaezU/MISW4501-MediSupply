from typing import Annotated
from pydantic import EmailStr, Field, field_validator
from src.errors.errors import BadRequestException
from src.schemas.base_schema import BaseSchema, UserBase, SellerBase, OrderBase


class UserCreateRequest(BaseSchema):
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
    address: Annotated[str, Field(min_length=1, max_length=255)]
    password: Annotated[str, Field(min_length=6, max_length=12)]

    @field_validator("phone")
    def validate_phone(cls, value: str) -> str:
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise BadRequestException("Phone must be between 9 and 15 digits")
        return value


class UserResponse(UserBase):
    seller: SellerBase
    orders: list[OrderBase]
