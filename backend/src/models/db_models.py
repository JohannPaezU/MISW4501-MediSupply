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
from src.models.enums.order_status import OrderStatus
from src.models.enums.user_role import UserRole
from src.models.enums.visit_status import VisitStatus


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
    geolocation_id: Mapped[Optional[str]] = mapped_column(
        String(36),
        ForeignKey("geolocations.id", ondelete="RESTRICT"),
        nullable=True,
        unique=True
    )
    zone: Mapped[Optional["Zone"]] = relationship(
        "Zone", back_populates="sellers", passive_deletes=True
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
    orders: Mapped[list["Order"]] = relationship(
        "Order", foreign_keys="[Order.client_id]", back_populates="client"
    )
    managed_orders: Mapped[list["Order"]] = relationship(
        "Order", foreign_keys="[Order.seller_id]", back_populates="seller"
    )
    geolocation: Mapped[Optional["Geolocation"]] = relationship(
        "Geolocation",
        back_populates="user",
        foreign_keys=[geolocation_id],
        uselist=False,
        cascade="save-update, merge",
        passive_deletes=True,
    )
    requested_visits: Mapped[list["Visit"]] = relationship(
        "Visit",
        foreign_keys="[Visit.client_id]",
        back_populates="client",
        cascade="all, delete-orphan",
    )
    assigned_visits: Mapped[list["Visit"]] = relationship(
        "Visit",
        foreign_keys="[Visit.seller_id]",
        back_populates="seller",
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
    sellers: Mapped[list["User"]] = relationship("User", back_populates="zone")
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
    price_per_unit: Mapped[float] = mapped_column(nullable=False)
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
    order_products: Mapped[list["OrderProduct"]] = relationship(
        "OrderProduct", back_populates="product"
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

    product: Mapped["Product"] = relationship("Product", back_populates="selling_plans")
    zone: Mapped[Optional["Zone"]] = relationship(
        "Zone", back_populates="selling_plans", passive_deletes=True
    )
    seller: Mapped[Optional["User"]] = relationship(
        "User", back_populates="selling_plans", passive_deletes=True
    )


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


class Order(Base):
    __tablename__ = "orders"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    comments: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    delivery_date: Mapped[date] = mapped_column(Date, nullable=False)
    status: Mapped[OrderStatus] = mapped_column(
        Enum(OrderStatus), nullable=False, default=OrderStatus.RECEIVED
    )
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
    )
    seller_id: Mapped[Optional[str]] = mapped_column(
        String(36),
        ForeignKey("users.id", ondelete="SET NULL"),
        nullable=True,
    )
    client_id: Mapped[str] = mapped_column(
        String(36),
        ForeignKey("users.id", ondelete="RESTRICT"),
        nullable=False,
    )
    distribution_center_id: Mapped[str] = mapped_column(
        String(36),
        ForeignKey("distribution_centers.id", ondelete="RESTRICT"),
        nullable=False,
    )
    seller: Mapped[Optional["User"]] = relationship(
        "User", foreign_keys=[seller_id], back_populates="managed_orders"
    )
    client: Mapped["User"] = relationship(
        "User", foreign_keys=[client_id], back_populates="orders"
    )
    distribution_center: Mapped["DistributionCenter"] = relationship(
        "DistributionCenter", back_populates="orders"
    )
    order_products: Mapped[list["OrderProduct"]] = relationship(
        "OrderProduct", back_populates="order", cascade="all, delete-orphan"
    )


class OrderProduct(Base):
    __tablename__ = "order_products"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    quantity: Mapped[int] = mapped_column(Integer, nullable=False)
    order_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("orders.id", ondelete="CASCADE"), nullable=False
    )
    product_id: Mapped[str] = mapped_column(
        String(36), ForeignKey("products.id", ondelete="RESTRICT"), nullable=False
    )
    order: Mapped["Order"] = relationship("Order", back_populates="order_products")
    product: Mapped["Product"] = relationship(
        "Product", back_populates="order_products"
    )


class Geolocation(Base):
    __tablename__ = "geolocations"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )
    address: Mapped[str] = mapped_column(String(255), nullable=False)
    latitude: Mapped[float] = mapped_column(nullable=False)
    longitude: Mapped[float] = mapped_column(nullable=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc),
        nullable=False,
    )
    user: Mapped[Optional["User"]] = relationship(
        "User",
        back_populates="geolocation",
        uselist=False,
        passive_deletes=True,
    )


class Visit(Base):
    __tablename__ = "visits"

    id: Mapped[str] = mapped_column(
        String(36), primary_key=True, index=True, default=lambda: str(uuid.uuid4())
    )
    expected_date: Mapped[datetime] = mapped_column(DateTime(timezone=True), nullable=False)
    visit_date: Mapped[Optional[datetime]] = mapped_column(DateTime(timezone=True), nullable=True)
    observations: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    visual_evidence_url: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    status: Mapped[VisitStatus] = mapped_column(Enum(VisitStatus), nullable=False, default=VisitStatus.PENDING)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True),
        default=lambda: datetime.now(timezone.utc),
        nullable=False,
    )
    expected_geolocation_id: Mapped[str] = mapped_column(
        String(36),
        ForeignKey("geolocations.id", ondelete="RESTRICT"),
        nullable=False,
    )
    report_geolocation_id: Mapped[Optional[str]] = mapped_column(
        String(36),
        ForeignKey("geolocations.id", ondelete="SET NULL"),
        nullable=True,
    )
    client_id: Mapped[str] = mapped_column(
        String(36),
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
    )
    seller_id: Mapped[str] = mapped_column(
        String(36),
        ForeignKey("users.id", ondelete="RESTRICT"),
        nullable=False,
    )
    expected_geolocation: Mapped["Geolocation"] = relationship(
        "Geolocation",
        foreign_keys=[expected_geolocation_id],
        passive_deletes=True,
    )
    report_geolocation: Mapped["Geolocation"] = relationship(
        "Geolocation",
        foreign_keys=[report_geolocation_id],
        passive_deletes=True,
    )
    client: Mapped["User"] = relationship(
        "User", foreign_keys=[client_id], back_populates="requested_visits"
    )
    seller: Mapped["User"] = relationship(
        "User", foreign_keys=[seller_id], back_populates="assigned_visits"
    )
