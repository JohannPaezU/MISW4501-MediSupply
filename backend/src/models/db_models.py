import uuid
from datetime import date, datetime, timezone
from typing import Optional

from sqlalchemy import (
    Boolean,
    Date,
    DateTime,
    Enum,
    ForeignKey,
    Integer,
    String,
    UniqueConstraint,
)
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
    seller_id: Mapped[Optional[str]] = mapped_column(
        String(36),
        ForeignKey("users.id", ondelete="SET NULL"),
        nullable=True,
    )
    zone: Mapped[Optional["Zone"]] = relationship(
        "Zone", back_populates="users", passive_deletes=True
    )
    seller: Mapped[Optional["User"]] = relationship(
        "User",
        remote_side="User.id",
        back_populates="clients",
    )
    clients: Mapped[list["User"]] = relationship(
        "User",
        back_populates="seller",
        cascade="all, delete-orphan",
    )
    otps: Mapped[list["OTP"]] = relationship(
        "OTP", back_populates="user", cascade="all, delete-orphan"
    )
    selling_plans: Mapped[list["SellingPlan"]] = relationship(
        "SellingPlan", back_populates="seller"
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
    users: Mapped[list["User"]] = relationship("User", back_populates="zone")
    selling_plans: Mapped[list["SellingPlan"]] = relationship(
        "SellingPlan", back_populates="zone"
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
    products: Mapped[list["Product"]] = relationship(
        "Product", back_populates="provider", cascade="all, delete-orphan"
    )

    @validates("phone")
    def validate_phone(self, _, value: str) -> str:  # pragma: no cover
        if not value.isdigit() or len(value) < 9 or len(value) > 15:
            raise ValueError("Phone must be between 9 and 15 digits")
        return value


class Product(Base):
    __tablename__ = "products"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    details: Mapped[str] = mapped_column(String(255), nullable=False)
    store: Mapped[str] = mapped_column(String(100), nullable=False)
    batch: Mapped[str] = mapped_column(String(50), nullable=False)
    image_url: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    due_date: Mapped[date] = mapped_column(Date, nullable=False)
    stock: Mapped[int] = mapped_column(Integer, nullable=False)
    price_per_unite: Mapped[float] = mapped_column(nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )
    provider_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("providers.id", ondelete="CASCADE"), nullable=False
    )
    provider: Mapped["Provider"] = relationship("Provider", back_populates="products")
    selling_plans: Mapped[list["SellingPlan"]] = relationship(
        "SellingPlan", back_populates="product", cascade="all, delete-orphan"
    )


class SellingPlan(Base):
    __tablename__ = "selling_plans"
    __table_args__ = (
        UniqueConstraint(
            "period",
            "product_id",
            "zone_id",
            "seller_id",
            name="selling_plan_unique_constraint",
        ),
    )

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    period: Mapped[str] = mapped_column(String(20), nullable=False)
    goal: Mapped[int] = mapped_column(Integer, nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )
    product_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("products.id", ondelete="RESTRICT"), nullable=False
    )
    zone_id: Mapped[Optional[str]] = mapped_column(
        String(36),
        ForeignKey("zones.id", ondelete="SET NULL"),
        nullable=True,
    )
    seller_id: Mapped[Optional[str]] = mapped_column(
        String(36),
        ForeignKey("users.id", ondelete="SET NULL"),
        nullable=True,
    )

    product: Mapped["Product"] = relationship("Product")
    zone: Mapped[Optional["Zone"]] = relationship("Zone", passive_deletes=True)
    seller: Mapped[Optional["User"]] = relationship("User", passive_deletes=True)

"""
class DistributionCenter(Base):
    __tablename__ = "distribution_centers"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    name: Mapped[str] = mapped_column(String(100), nullable=False)
    address: Mapped[str] = mapped_column(String(255), nullable=False)
    city: Mapped[str] = mapped_column(String(100), nullable=False)
    country: Mapped[str] = mapped_column(String(100), nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )
    orders: Mapped[list["Order"]] = relationship(
        "Order", back_populates="distribution_center", cascade="all, delete-orphan"
    )
"""