from typing import Annotated

from pydantic import EmailStr, Field

from src.schemas.base_schema import BaseSchema
from src.schemas.user_schema import UserBase


class LoginRequest(BaseSchema):
    email: Annotated[EmailStr, Field(min_length=5, max_length=120)]
    password: Annotated[str, Field(min_length=6, max_length=12)]


class LoginResponse(BaseSchema):
    message: str
    otp_expiration_minutes: int


class OTPVerifyRequest(BaseSchema):
    email: Annotated[EmailStr, Field(min_length=5, max_length=120)]
    otp_code: Annotated[
        str,
        Field(
            ...,
            min_length=6,
            max_length=6,
            description="6-digit OTP code sent via email",
        ),
    ]


class OTPVerifyResponse(BaseSchema):
    message: str
    access_token: str
    token_type: str
    user: UserBase
