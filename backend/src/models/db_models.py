import uuid
from datetime import datetime, timezone

from sqlalchemy import Column, DateTime, Enum, String
from sqlalchemy.orm import validates

from src.db.database import Base
from src.models.enums.user_role import UserRole


class User(Base):
    __tablename__ = "user"
    id = Column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    full_name = Column(String(100), nullable=False)
    email = Column(String(120), unique=True, index=True, nullable=False)
    hashed_password = Column(String(60), nullable=False)
    phone = Column(String(10), nullable=False)
    nit = Column(String(50), nullable=False)
    address = Column(String(255), nullable=False)
    role = Column(Enum(UserRole), nullable=False)
    created_at = Column(
        DateTime, nullable=False, default=lambda: datetime.now(timezone.utc)
    )

    @validates("phone")
    def validate_phone(self, _, value: str) -> str:  # pragma: no cover
        if not value.isdigit() or len(value) != 10:
            raise ValueError("Phone must be exactly 10 digits")
        return value
