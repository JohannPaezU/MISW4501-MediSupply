from datetime import date
from typing import Annotated

from pydantic import Field

from src.models.enums.order_status import OrderStatus
from src.schemas.base_schema import (
    BaseSchema,
    DistributionCenterBase,
    OrderBase,
    RouteBase,
)


class RouteCreateRequest(BaseSchema):
    name: Annotated[str, Field(max_length=100)]
    vehicle_plate: Annotated[str, Field(max_length=20)]
    restrictions: Annotated[str | None, Field(max_length=255)] = None
    distribution_center_id: Annotated[str, Field(min_length=36, max_length=36)]
    order_ids: Annotated[list[str], Field(min_length=1)]


class RouteResponse(RouteBase):
    distribution_center: DistributionCenterBase
    orders: list[OrderBase]


class RouteMinimalResponse(RouteBase):
    distribution_center: DistributionCenterBase


class GetRoutesResponse(BaseSchema):
    total_count: int
    routes: list[RouteMinimalResponse]


class RouteMapDetail(BaseSchema):
    order_id: str
    order_status: OrderStatus
    delivery_date: date
    client_name: str
    client_address: str
    client_phone: str
    latitude: float
    longitude: float


class RouteMapResponse(RouteBase):
    total_count: int
    stops: list[RouteMapDetail]
