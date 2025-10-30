from src.schemas.base_schema import OrderBase, BaseSchema
from src.schemas.order_schema import OrderProductDetail
from src.schemas.seller_schema import SellerMinimalResponse


class OrderReportResponse(OrderBase):
    seller: SellerMinimalResponse
    products: list[OrderProductDetail]


class GetOrderReportResponse(BaseSchema):
    total_count: int
    orders: list[OrderReportResponse]
