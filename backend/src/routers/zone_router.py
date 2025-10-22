from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.enums.user_role import UserRole
from src.schemas.zone_schema import GetZonesResponse, ZoneResponse
from src.services.zone_service import get_zones, get_zone_by_id

zone_router = APIRouter(
    tags=["Zones"],
    prefix="/zones",
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
)


@zone_router.get(
    "",
    response_model=GetZonesResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all zones",
    description="""
Retrieve a list of all zones.

### Response
Returns a list of zones with the following details for each zone:
- **id**: Unique zone ID
- **description**: Description of the zone
- **created_at**: Timestamp of zone creation
""",
)
async def get_all_zones(db: Session = Depends(get_db)) -> GetZonesResponse:
    zones = get_zones(db=db)

    return GetZonesResponse(total_count=len(zones), zones=zones)


@zone_router.get(
    "/{zone_id}",
    response_model=ZoneResponse,
    status_code=status.HTTP_200_OK,
    summary="Get zone by ID",
    description="""
Retrieve a zone by its unique ID.

### Path Parameters
- **zone_id**: Unique ID of the zone to retrieve

### Response
- **id**: Unique zone ID
- **description**: Description of the zone
- **created_at**: Timestamp of zone creation
- **sellers**: List of associated sellers
- **selling_plans**: List of associated selling plans
""",
)
async def get_zone(*, zone_id: str, db: Session = Depends(get_db)) -> ZoneResponse:
    zone = get_zone_by_id(db=db, zone_id=zone_id)
    if not zone:
        raise NotFoundException("Zone not found")

    return zone
