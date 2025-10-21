from datetime import datetime
from typing import Annotated

from pydantic import EmailStr, Field, field_validator

from src.errors.errors import BadRequestException
from src.models.enums.user_role import UserRole
from src.schemas.base_schema import BaseSchema


class UserBase(BaseSchema):
    id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    email: Annotated[EmailStr, Field(min_length=5, max_length=120)]
    full_name: Annotated[str, Field(min_length=1, max_length=100)]
    doi: Annotated[
        str,
        Field(
            min_length=1,
            max_length=50,
            description="Unique user identification number (NIT, RUC, ID, etc.)",
        ),
    ]
    address: Annotated[str | None, Field(min_length=1, max_length=255)] = None
    phone: Annotated[str, Field(min_length=9, max_length=15)]
    role: Annotated[UserRole | None, Field()] = None

    @field_validator("phone")
    def validate_phone(cls, value: str) -> str:
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise BadRequestException("Phone must be between 9 and 15 digits")
        return value


class UserCreateRequest(UserBase):
    address: Annotated[str, Field(min_length=1, max_length=255)]
    password: Annotated[str, Field(min_length=6, max_length=12)]


class UserCreateResponse(BaseSchema):
    id: str
    created_at: datetime
