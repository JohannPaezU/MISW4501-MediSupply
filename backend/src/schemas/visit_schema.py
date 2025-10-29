from datetime import date, datetime
from typing import Annotated

from pydantic import Field

from src.schemas.base_schema import (
    BaseSchema,
    VisitBase,
    SellerBase,
    UserBase,
    GeolocationBase,
)


class VisitCreateRequest(BaseSchema):
    expected_date: Annotated[date, Field()]
    address: Annotated[str | None, Field(min_length=1, max_length=255)] = None


class VisitReportRequest(BaseSchema):
    visit_id: Annotated[str, Field(min_length=36, max_length=36)]
    visit_date: Annotated[datetime | None, Field()] = None
    observations: Annotated[str | None, Field(min_length=1, max_length=255)] = None
    latitude: Annotated[float, Field()]
    longitude: Annotated[float, Field()]


class VisitBaseResponse(VisitBase):
    expected_geolocation: GeolocationBase
    report_geolocation: GeolocationBase | None = None


class VisitResponse(VisitBaseResponse):
    client: UserBase
    seller: SellerBase


class SellerVisitResponse(VisitBaseResponse):
    client: UserBase


class ClientVisitResponse(VisitBaseResponse):
    seller: SellerBase


class GetVisitsResponse(BaseSchema):
    total_count: int


class GetSellerVisitsResponse(GetVisitsResponse):
    visits: list[SellerVisitResponse]


class GetClientVisitsResponse(GetVisitsResponse):
    visits: list[ClientVisitResponse]

