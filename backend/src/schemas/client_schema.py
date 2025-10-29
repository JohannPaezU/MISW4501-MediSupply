from src.schemas.base_schema import (
    BaseSchema,
    UserBase,
)


class GetClientsResponse(BaseSchema):
    total_count: int
    clients: list[UserBase]
