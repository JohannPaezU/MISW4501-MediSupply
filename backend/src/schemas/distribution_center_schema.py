from src.schemas.base_schema import (
    BaseSchema,
    DistributionCenterBase,
    OrderBase,
    RouteBase,
)


class DistributionCenterResponse(DistributionCenterBase):
    orders: list[OrderBase]
    routes: list[RouteBase]


class GetDistributionCentersResponse(BaseSchema):
    total_count: int
    distribution_centers: list[DistributionCenterBase]
