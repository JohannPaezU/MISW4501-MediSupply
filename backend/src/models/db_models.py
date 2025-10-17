import uuid
from datetime import datetime, timezone
from typing import Optional

from sqlalchemy import Boolean, DateTime, Enum, ForeignKey, Integer, String
from sqlalchemy.orm import Mapped, mapped_column, relationship, validates

from src.db.database import Base
from src.models.enums.user_role import UserRole


class User(Base):
    __tablename__ = "users"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    full_name: Mapped[str] = mapped_column(String(100), nullable=False)
    email: Mapped[str] = mapped_column(
        String(120), unique=True, index=True, nullable=False
    )
    hashed_password: Mapped[str] = mapped_column(String(60), nullable=False)
    phone: Mapped[str] = mapped_column(String(15), nullable=False)
    doi: Mapped[str] = mapped_column(String(50), nullable=False, unique=True)
    address: Mapped[str] = mapped_column(String(255), nullable=True)
    role: Mapped[UserRole] = mapped_column(Enum(UserRole), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )
    zone_id: Mapped[Optional[str]] = mapped_column(
        String(36),
        ForeignKey("zones.id", ondelete="SET NULL"),
        nullable=True,
    )
    zone: Mapped[Optional["Zone"]] = relationship(
        "Zone", back_populates="users", passive_deletes=True
    )
    otps: Mapped[list["OTP"]] = relationship(
        "OTP", back_populates="user", cascade="all, delete-orphan"
    )

    @validates("phone")
    def validate_phone(self, _, value: str) -> str:  # pragma: no cover
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise ValueError("Phone must be between 9 and 15 digits")
        return value


class OTP(Base):
    __tablename__ = "otps"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    code: Mapped[str] = mapped_column(String(6), nullable=False)
    expires_at: Mapped[datetime] = mapped_column(DateTime, nullable=False)
    expiration_minutes: Mapped[int] = mapped_column(Integer, nullable=False)
    is_used: Mapped[bool] = mapped_column(Boolean, default=False)
    user_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=False
    )

    user: Mapped["User"] = relationship("User", back_populates="otps")


class Zone(Base):
    __tablename__ = "zones"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    description: Mapped[str] = mapped_column(String(255), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )
    users: Mapped[list["User"]] = relationship(
        "User", back_populates="zone"
    )


class Provider(Base):
    __tablename__ = "providers"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    rit: Mapped[str] = mapped_column(String(50), nullable=False, unique=True)
    city: Mapped[str] = mapped_column(String(100), nullable=False)
    country: Mapped[str] = mapped_column(String(100), nullable=False)
    image_url: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    email: Mapped[str] = mapped_column(
        String(120), unique=True, index=True, nullable=False
    )
    phone: Mapped[str] = mapped_column(String(15), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )

    @validates("phone")
    def validate_phone(self, _, value: str) -> str:  # pragma: no cover
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise ValueError("Phone must be between 9 and 15 digits")
        return value
