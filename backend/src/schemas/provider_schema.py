from datetime import datetime
from typing import Annotated

from pydantic import BaseModel, EmailStr, Field, field_validator

from src.errors.errors import BadRequestException


class ProviderBase(BaseModel):
    id: Annotated[str | None, Field(min_length=36, max_length=36)] = None
    name: Annotated[str, Field(min_length=1, max_length=100)]
    rit: Annotated[str, Field(min_length=1, max_length=50)]
    city: Annotated[str, Field(min_length=1, max_length=100)]
    country: Annotated[str, Field(min_length=1, max_length=100)]
    image_url: Annotated[str | None, Field(max_length=255)] = None
    email: Annotated[EmailStr, Field(max_length=120)]
    phone: Annotated[str, Field(min_length=9, max_length=15)]
    created_at: Annotated[datetime | None, Field()] = None

    @field_validator("phone")
    def validate_phone(cls, value: str) -> str:
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise BadRequestException("Phone must be between 9 and 15 digits")
        return value

    model_config = {"str_strip_whitespace": True, "from_attributes": True}


class ProviderCreateRequest(ProviderBase):
    pass


class ProviderCreateResponse(ProviderBase):
    pass


class GetProvidersResponse(BaseModel):
    total_count: int
    providers: list[ProviderBase]
