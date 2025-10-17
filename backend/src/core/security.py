from datetime import datetime, timedelta, timezone
from typing import Any, Callable

import bcrypt
import jwt
from fastapi import Depends
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session

from src.core.config import settings
from src.core.logging_config import logger
from src.db.database import get_db
from src.errors.errors import ApiError, ForbiddenException, UnauthorizedException
from src.models.db_models import User
from src.models.enums.user_role import UserRole

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/auth/login")


def hash_password(password: str) -> str:
    pw_bytes = password.encode("utf-8")
    salt = bcrypt.gensalt(rounds=12)
    hashed = bcrypt.hashpw(pw_bytes, salt)
    return hashed.decode("utf-8")


def verify_password(plain_password: str, hashed_password: str) -> bool:
    return bcrypt.checkpw(
        plain_password.encode("utf-8"), hashed_password.encode("utf-8")
    )


def create_access_token(data: dict[str, Any]) -> str:
    to_encode = data.copy()
    expire = datetime.now(timezone.utc) + timedelta(
        minutes=settings.access_token_expire_minutes
    )
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(
        to_encode, settings.jwt_secret_key, algorithm=settings.jwt_algorithm
    )

    return encoded_jwt


def get_current_user(
    token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)
) -> User:  # pragma: no cover
    try:
        payload = jwt.decode(
            token, settings.jwt_secret_key, algorithms=[settings.jwt_algorithm]
        )
        sub: str = payload.get("sub")
        role: str = payload.get("role")
        if sub is None or role is None:
            raise UnauthorizedException("Token is invalid or has expired.")
        user: User | None = db.query(User).filter_by(id=sub).first()
        if not user:
            raise UnauthorizedException("Token is invalid or has expired.")

        return user
    except jwt.PyJWTError as e:
        logger.error(f"JWT decoding error: {e}")
        raise UnauthorizedException("Token is invalid or has expired.") from e
    except ApiError:
        raise
    except Exception as e:
        logger.error(f"An unexpected error occurred: {e}")
        raise ApiError("An error occurred while processing the token.") from e


def require_roles(
    allowed_roles: list[UserRole],
) -> Callable[[User], User]:  # pragma: no cover
    def role_checker(current_user: User = Depends(get_current_user)) -> User:
        if current_user.role not in allowed_roles:
            raise ForbiddenException(
                f"Access denied: requires one of the following roles: {', '.join(r.value for r in allowed_roles)}"
            )

        return current_user

    return role_checker
