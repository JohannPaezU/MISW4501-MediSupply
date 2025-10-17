from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.db.database import get_db
from src.errors.errors import UnauthorizedException
from src.schemas.auth_schema import (
    LoginRequest,
    LoginResponse,
    OTPVerifyRequest,
    OTPVerifyResponse,
)
from src.schemas.user_schema import UserCreateRequest, UserCreateResponse
from src.services.auth_service import login_user, verify_otp_and_get_token
from src.services.user_service import create_user, get_user_by_email

auth_router = APIRouter(prefix="/auth", tags=["Auth"])


@auth_router.post(
    "/register",
    response_model=UserCreateResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register a new user",
    description="""
Create a new user account.

### Request Body
- **email**: User's email address (5–120 characters)
- **full_name**: Full name (1–100 characters)
- **nit**: NIT (1–50 characters)
- **address**: Address (1–255 characters)
- **phone**: Phone number (9–15 digits)
- **role**: Either `institutional` or `commercial`
- **password**: Between 6–12 characters

### Response
Returns the created user's `id` and `created_at` timestamp.
""",
)
async def register_user(
    *,
    user_create_request: UserCreateRequest,
    db: Session = Depends(get_db),
) -> UserCreateResponse:
    return create_user(db=db, user_create_request=user_create_request)


@auth_router.post(
    "/login",
    response_model=LoginResponse,
    status_code=status.HTTP_200_OK,
    summary="Login and request OTP",
    description="""
Authenticate a user with their email and password.
If the credentials are valid, an OTP is generated and sent via email.

### Request Body
- **email**: Registered email
- **password**: Plain-text password

### Response
- **message**: Confirmation that the OTP was sent.
- **otp_expiration_minutes**: Time window before the OTP expires.
""",
)
async def login(
    *, login_request: LoginRequest, db: Session = Depends(get_db)
) -> LoginResponse:
    otp_expiration_minutes = login_user(db=db, login_request=login_request)

    return LoginResponse(
        message="OTP generated successfully",
        otp_expiration_minutes=otp_expiration_minutes,
    )


@auth_router.post(
    "/verify-otp",
    response_model=OTPVerifyResponse,
    status_code=status.HTTP_200_OK,
    summary="Verify OTP and get access token",
    description="""
Verify the 6-digit OTP sent to the user's email.
If valid, a JWT access token is returned.

### Request Body
- **email**: Registered email of the user
- **otp_code**: 6-digit OTP sent via email

### Response
- **message**: Confirmation that OTP was verified
- **access_token**: JWT access token valid for the configured expiration
- **token_type**: Token type, typically 'bearer'
""",
)
async def verify_otp(
    *,
    otp_verify_request: OTPVerifyRequest,
    db: Session = Depends(get_db),
) -> OTPVerifyResponse:
    user = get_user_by_email(db=db, email=otp_verify_request.email)
    if not user:
        raise UnauthorizedException("Invalid or expired OTP")

    access_token = verify_otp_and_get_token(
        db=db, otp_verify_request=otp_verify_request, user=user
    )

    return OTPVerifyResponse(
        message="OTP verified successfully",
        access_token=access_token,
        token_type="bearer",
        user=user,
    )
