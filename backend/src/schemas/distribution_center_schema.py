from src.schemas.base_schema import BaseSchema, DistributionCenterBase, OrderBase


class DistributionCenterResponse(DistributionCenterBase):
    orders: list[OrderBase]


class GetDistributionCentersResponse(BaseSchema):
    total_count: int
    distribution_centers: list[DistributionCenterBase]
