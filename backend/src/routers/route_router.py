from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.enums.user_role import UserRole
from src.schemas.route_schema import (
    GetRoutesResponse,
    RouteCreateRequest,
    RouteMapDetail,
    RouteMapResponse,
    RouteResponse,
)
from src.services.route_service import create_route, get_route_by_id, get_routes

route_router = APIRouter(
    tags=["Routes"],
    prefix="/routes",
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
)


@route_router.post(
    "",
    response_model=RouteResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register a new route",
    description="""
Create a new route in the system.

### Request Body
- **name**: Name of the route (max 100 characters).
- **vehicle_plate**: Vehicle plate associated with the route (max 20 characters).
- **restrictions**: Optional restrictions for the route (max 255 characters).
- **distribution_center_id**: ID of the distribution center (36 characters).
- **order_ids**: List of order IDs to be included in the route (at least 1).

### Response
- **id**: Unique identifier of the route.
- **name**: Name of the route.
- **vehicle_plate**: Vehicle plate associated with the route.
- **restrictions**: Restrictions for the route (if any).
- **delivery_deadline**: Delivery deadline for the route.
- **created_at**: Timestamp when the route was created.
- **distribution_center**: Information about the distribution center.
- **orders**: List of orders included in the route with their details.
""",
)
async def register_route(
    *,
    route_create_request: RouteCreateRequest,
    db: Session = Depends(get_db),
) -> RouteResponse:
    route = create_route(db=db, route_create_request=route_create_request)

    return route


@route_router.get(
    "",
    response_model=GetRoutesResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all routes",
    description="""
Retrieve a list of all routes in the system.

### Response
- **total_count**: Total number of routes.
- **routes**: List of routes with the basic route information.
    - **id**: Unique identifier of the route.
    - **name**: Name of the route.
    - **vehicle_plate**: Vehicle plate associated with the route.
    - **restrictions**: Restrictions for the route (if any).
    - **delivery_deadline**: Delivery deadline for the route.
    - **created_at**: Timestamp when the route was created.
""",
)
async def get_all_routes(
    *,
    db: Session = Depends(get_db),
) -> GetRoutesResponse:
    routes = get_routes(db=db)

    return GetRoutesResponse(total_count=len(routes), routes=routes)


@route_router.get(
    "/{route_id}",
    response_model=RouteResponse,
    status_code=status.HTTP_200_OK,
    summary="Get route by ID",
    description="""
Retrieve detailed information about a specific route by its ID.

### Path Parameters
- **route_id**: Unique identifier of the route (36 characters).

### Response
- **id**: Unique identifier of the route.
- **name**: Name of the route.
- **vehicle_plate**: Vehicle plate associated with the route.
- **restrictions**: Restrictions for the route (if any).
- **delivery_deadline**: Delivery deadline for the route.
- **created_at**: Timestamp when the route was created.
- **distribution_center**: Information about the distribution center.
- **orders**: List of orders included in the route with their details.
""",
)
async def get_route(
    *,
    route_id: str,
    db: Session = Depends(get_db),
) -> RouteResponse:
    route = get_route_by_id(db=db, route_id=route_id)
    if not route:
        raise NotFoundException("Route not found")

    return route


@route_router.get(
    "/{route_id}/map",
    response_model=RouteMapResponse,
    status_code=status.HTTP_200_OK,
    summary="Get route map by ID",
    description="""
Retrieve the map details of a specific route by its ID.

### Path Parameters
- **route_id**: Unique identifier of the route (36 characters).

### Response
- **id**: Unique identifier of the route.
- **name**: Name of the route.
- **vehicle_plate**: Vehicle plate associated with the route.
- **restrictions**: Restrictions for the route (if any).
- **delivery_deadline**: Delivery deadline for the route.
- **created_at**: Timestamp when the route was created.
- **total_count**: Total number of stops in the route.
- **stops**: List of stops in the route with their details.
    - **order_id**: Unique identifier of the order.
    - **order_status**: Current status of the order.
    - **delivery_date**: Date when the order should be delivered.
    - **client_name**: Name of the client.
    - **client_address**: Address of the client.
    - **client_phone**: Phone number of the client.
    - **latitude**: Latitude of the client's location.
    - **longitude**: Longitude of the client's location.
""",
)
async def get_route_map(
    *,
    route_id: str,
    db: Session = Depends(get_db),
) -> RouteMapResponse:
    route = get_route_by_id(db=db, route_id=route_id)
    if not route:
        raise NotFoundException("Route not found")

    stops = []
    for order in route.orders:
        stops.append(
            RouteMapDetail(
                order_id=order.id,
                order_status=order.status,
                delivery_date=order.delivery_date,
                client_name=order.client.full_name,
                client_address=order.client.address,
                client_phone=order.client.phone,
                latitude=order.client.geolocation.latitude,  # type: ignore
                longitude=order.client.geolocation.longitude,  # type: ignore
            )
        )

    return RouteMapResponse(
        **route.__dict__,
        total_count=len(stops),
        stops=stops,
    )
