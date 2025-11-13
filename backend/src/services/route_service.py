from sqlalchemy import func
from sqlalchemy.orm import Session

from src.errors.errors import BadRequestException, NotFoundException
from src.models.db_models import Order, Route
from src.schemas.route_schema import RouteCreateRequest
from src.services.distribution_center_service import get_distribution_center_by_id


def create_route(
    *,
    db: Session,
    route_create_request: RouteCreateRequest,
) -> Route | None:
    _validate_route_request(db=db, route_create_request=route_create_request)
    min_delivery_date = (
        db.query(func.min(Order.delivery_date))
        .filter(Order.id.in_(route_create_request.order_ids))
        .scalar()
    )
    orders = db.query(Order).filter(Order.id.in_(route_create_request.order_ids)).all()
    route = Route(
        name=route_create_request.name,
        vehicle_plate=route_create_request.vehicle_plate,
        restrictions=route_create_request.restrictions,
        delivery_deadline=min_delivery_date,
        distribution_center_id=route_create_request.distribution_center_id,
        orders=orders,  # type: ignore
    )
    db.add(route)
    db.commit()
    db.refresh(route)

    return route


def get_routes(*, db: Session) -> list[Route]:
    return db.query(Route).all()  # type: ignore


def get_route_by_id(*, db: Session, route_id: str) -> Route | None:
    return db.query(Route).filter(Route.id == route_id).first()


def _validate_route_request(
    *, db: Session, route_create_request: RouteCreateRequest
) -> None:
    duplicated_ids = set(
        x
        for x in route_create_request.order_ids
        if route_create_request.order_ids.count(x) > 1
    )
    if duplicated_ids:
        raise BadRequestException(
            f"Duplicated order IDs in route: {', '.join(duplicated_ids)}"
        )

    orders = db.query(Order).filter(Order.id.in_(route_create_request.order_ids)).all()
    existing_order_ids = {order.id for order in orders}
    missing_order_ids = set(route_create_request.order_ids) - existing_order_ids  # noqa
    if missing_order_ids:
        raise NotFoundException(f"Orders not found: {', '.join(missing_order_ids)}")

    distribution_center = get_distribution_center_by_id(
        db=db, distribution_center_id=route_create_request.distribution_center_id
    )
    if not distribution_center:
        raise NotFoundException("Distribution center not found")

    for order in orders:
        if order.distribution_center_id != distribution_center.id:
            raise BadRequestException(
                f"Order ID {order.id} does not belong to the specified distribution center"
            )
