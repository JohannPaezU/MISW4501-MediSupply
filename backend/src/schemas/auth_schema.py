from typing import Annotated
from pydantic import BaseModel, EmailStr, Field


class LoginRequest(BaseModel):
    email: Annotated[EmailStr, Field(min_length=5, max_length=120)]
    password: Annotated[str, Field(min_length=6, max_length=12)]


class LoginResponse(BaseModel):
    message: str
    otp_expiration_minutes: int


class OTPVerifyRequest(BaseModel):
    email: Annotated[EmailStr, Field(min_length=5, max_length=120)]
    otp_code: Annotated[str, Field(..., min_length=6, max_length=6, description="6-digit OTP code sent via email")]


class OTPVerifyResponse(BaseModel):
    message: str
    access_token: str
    token_type: str = "bearer"
