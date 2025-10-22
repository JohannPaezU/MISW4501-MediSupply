from src.schemas.base_schema import BaseSchema, ZoneBase, SellerBase, SellingPlanBase


class ZoneResponse(ZoneBase):
    sellers: list[SellerBase]
    selling_plans: list[SellingPlanBase]


class GetZonesResponse(BaseSchema):
    total_count: int
    zones: list[ZoneBase]
