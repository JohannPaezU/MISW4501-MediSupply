from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.db.database import get_db
from src.schemas.user_schema import UserCreateRequest, UserCreateResponse
from src.services.user_service import create_user

auth_router = APIRouter(prefix="/auth", tags=["Auth"])


@auth_router.post(
    "/register",
    response_model=UserCreateResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register a new user",
    description="""
Create a new user account.

**Request Body**:
- `email`: User's email address (5-120 characters).
- `full_name`: User's full name (1-100 characters).
- `nit`: User's NIT (1-50 characters).
- `address`: User's address (1-255 characters).
- `phone`: User's phone number (exactly 10 digits).
- `role`: User's role, either "institutional" or "commercial".
- `password`: User's password (6-12 characters).

**Returns**: The created user's ID and creation timestamp.
""",
)
def register_user(
    user_in: UserCreateRequest, db: Session = Depends(get_db)
) -> UserCreateResponse:
    return create_user(db=db, user_create_request=user_in)
