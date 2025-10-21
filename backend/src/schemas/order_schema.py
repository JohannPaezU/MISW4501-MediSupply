from pydantic import Field
from typing import List, Optional, Annotated
from datetime import date

from src.schemas.base_schema import BaseSchema


class OrderProductRequest(BaseSchema):
    product_id: Annotated[str, Field(min_length=36, max_length=36)]
    quantity: Annotated[int, Field(gt=0)]


class OrderBaseRequest(BaseSchema):
    comments: Optional[Annotated[str, Field(max_length=255)]] = None
    delivery_date: Annotated[date, Field()]
    distribution_center_id: Annotated[str, Field(min_length=36, max_length=36)]
    products: List[OrderProductRequest]


class OrderCreateBySellerRequest(OrderBaseRequest):
    client_id: Annotated[str, Field(min_length=36, max_length=36)]


class OrderCreateByClientRequest(OrderBaseRequest):
    pass
