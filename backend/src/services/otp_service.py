import os
from datetime import datetime, timedelta, timezone
import random
from sqlalchemy.orm import Session

from src.core.config import settings
from src.errors.errors import UnauthorizedException
from src.models.db_models import User, OTP


def create_otp(*, db: Session, user: User) -> OTP:
    otp_code = f"{random.randint(0, 999999):06d}"
    expires_at = datetime.now(timezone.utc) + timedelta(minutes=settings.otp_expiration_minutes)

    otp = OTP(
        code=otp_code,
        expires_at=expires_at,
        expiration_minutes=settings.otp_expiration_minutes,
        is_used=False,
        user_id=user.id,
    )
    db.add(otp)
    db.commit()
    db.refresh(otp)

    return otp


def verify_otp(*, db: Session, user: User, otp_code: str) -> None:
    otp = db.query(OTP).filter(
        OTP.user_id == user.id,
        OTP.code == otp_code,
        OTP.is_used.is_(False),
        OTP.expires_at > datetime.now(timezone.utc)).first()

    if not otp:
        raise UnauthorizedException("Invalid or expired OTP")

    otp.is_used = True
    db.commit()
