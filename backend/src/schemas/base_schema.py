from datetime import date, datetime
from typing import Any

from pydantic import BaseModel, model_serializer

from src.models.enums.order_status import OrderStatus
from src.models.enums.user_role import UserRole
from src.models.enums.visit_status import VisitStatus


class BaseSchema(BaseModel):
    model_config = {
        "str_strip_whitespace": True,
        "from_attributes": True,
        "extra": "ignore",
    }

    @model_serializer(mode="wrap")
    def _serialize(self, serializer: Any, info):  # pragma: no cover
        data = serializer(self)
        if isinstance(data, dict):
            return {k: v for k, v in data.items() if v is not None}
        return data


class UserBase(BaseSchema):
    id: str
    full_name: str
    email: str
    phone: str
    doi: str
    address: str | None = None
    role: UserRole
    created_at: datetime


class ZoneBase(BaseSchema):
    id: str
    description: str
    created_at: datetime


class SellerBase(UserBase):
    pass


class ProviderBase(BaseSchema):
    id: str
    name: str
    rit: str
    city: str
    country: str
    image_url: str | None = None
    email: str
    phone: str
    created_at: datetime


class ProductBase(BaseSchema):
    id: str
    name: str
    details: str
    store: str
    batch: str
    image_url: str | None = None
    due_date: date
    stock: int
    price_per_unit: float
    created_at: datetime


class SellingPlanBase(BaseSchema):
    id: str
    period: str
    goal: int
    created_at: datetime


class DistributionCenterBase(BaseSchema):
    id: str
    name: str
    address: str
    city: str
    country: str
    created_at: datetime


class OrderBase(BaseSchema):
    id: str
    comments: str | None = None
    delivery_date: date
    status: OrderStatus
    created_at: datetime


class GeolocationBase(BaseSchema):
    id: str
    address: str | None = None
    latitude: float
    longitude: float
    created_at: datetime


class VisitBase(BaseSchema):
    id: str
    expected_date: date
    visit_date: datetime | None = None
    observations: str | None = None
    visual_evidence_url: str | None = None
    status: VisitStatus
    created_at: datetime


class RouteBase(BaseSchema):
    id: str
    name: str
    vehicle_plate: str
    restrictions: str | None = None
    delivery_deadline: date
    created_at: datetime
