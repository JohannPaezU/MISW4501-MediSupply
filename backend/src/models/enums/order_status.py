import enum


class OrderStatus(enum.Enum):
    RECEIVED = "received"
    PREPARING = "preparing"
    IN_TRANSIT = "in_transit"
    DELIVERED = "delivered"
    RETURNED = "returned"
