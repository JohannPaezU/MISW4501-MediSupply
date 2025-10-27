from collections import defaultdict

from fastapi import APIRouter, Depends, status
from fastapi.routing import APIRoute
from sqlalchemy.orm import Session
from starlette.responses import JSONResponse

from src.core.security import get_current_user, require_roles
from src.db.database import get_db
from src.errors.errors import UnauthorizedException
from src.models.db_models import User
from src.models.enums.user_role import UserRole
from src.schemas.auth_schema import (
    LoginRequest,
    LoginResponse,
    OTPVerifyRequest,
    OTPVerifyResponse,
)
from src.schemas.user_schema import UserCreateRequest, UserResponse
from src.services.auth_service import login_user, verify_otp_and_get_token
from src.services.user_service import create_user, get_user_by_email

auth_router = APIRouter(prefix="/auth", tags=["Auth"])


@auth_router.post(
    "/register",
    response_model=UserResponse,
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
- **id**: Unique user ID
- **full_name**: Full name
- **email**: Email address
- **phone**: Phone number
- **doi**: NIT
- **address**: Address
- **role**: User role
- **created_at**: Timestamp of account creation
- **seller**: Associated seller details
- **orders**: List of associated orders
""",
)
async def register_user(
    *,
    user_create_request: UserCreateRequest,
    db: Session = Depends(get_db),
) -> UserResponse:
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


@auth_router.get(
    "/permissions",
    status_code=status.HTTP_200_OK,
    summary="Dynamically check accessible endpoints by role",
    description="Retrieve a list of API endpoints accessible to the current user based on their role.",
    dependencies=[
        Depends(
            require_roles(
                allowed_roles=[
                    UserRole.ADMIN,
                    UserRole.COMMERCIAL,
                    UserRole.INSTITUTIONAL,
                ]
            )
        )
    ],
)
def get_permissions(
    current_user: User = Depends(get_current_user),
) -> JSONResponse:  # pragma: no cover
    user_role = current_user.role.value
    grouped = defaultdict(set)
    from src.main import app

    for route in app.routes:
        if not isinstance(route, APIRoute):
            continue

        roles = []
        for dep in route.dependant.dependencies:
            if hasattr(dep.call, "allowed_roles"):
                roles.extend([r.value for r in dep.call.allowed_roles])
        for dep in getattr(route.dependant, "path_dependencies", []):
            if hasattr(dep.call, "allowed_roles"):
                roles.extend([r.value for r in dep.call.allowed_roles])

        roles = list(set(roles)) or ["public"]

        if "public" in roles or user_role in roles:
            grouped[(route.path, frozenset(roles))].update(
                route.methods - {"HEAD", "OPTIONS"}
            )

    allowed_endpoints = [
        {
            "path": path,
            "methods": sorted(list(methods)),
            "allowed_roles": sorted(list(roles)),
        }
        for (path, roles), methods in grouped.items()
    ]

    return JSONResponse(
        content={
            "user": {
                "id": str(current_user.id),
                "email": current_user.email,
                "role": current_user.role.value,
            },
            "allowed_endpoints": allowed_endpoints,
        }
    )
