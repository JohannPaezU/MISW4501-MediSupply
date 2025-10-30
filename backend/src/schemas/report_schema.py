from schemas.base_schema import BaseSchema, OrderBase
from schemas.order_schema import OrderProductDetail
from schemas.seller_schema import SellerMinimalResponse


class OrderReportResponse(OrderBase):
    seller: SellerMinimalResponse
    products: list[OrderProductDetail]


class GetOrderReportResponse(BaseSchema):
    total_count: int
    orders: list[OrderReportResponse]
