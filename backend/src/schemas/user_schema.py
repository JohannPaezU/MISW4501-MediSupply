from datetime import datetime
from typing import Annotated

from pydantic import BaseModel, EmailStr, Field, field_validator

from src.errors.errors import BadRequestException
from src.models.enums.user_role import UserRole


class UserBase(BaseModel):
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
    address: Annotated[str, Field(min_length=1, max_length=255)]
    phone: str
    role: UserRole

    @field_validator("phone", mode="before")
    def validate_phone(cls, value: str) -> str:
        if not value.isdigit() or 9 > len(value) > 15:
            raise BadRequestException("Phone must be between 9 and 15 digits")
        return value

    model_config = {"str_strip_whitespace": True, "from_attributes": True}


class UserCreateRequest(UserBase):
    password: Annotated[str, Field(min_length=6, max_length=12)]


class UserCreateResponse(BaseModel):
    id: str
    created_at: datetime
