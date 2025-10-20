from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.models.enums.user_role import UserRole
from src.schemas.zone_schema import GetZonesResponse
from src.services.zone_service import get_zones

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
Returns a list of zones with their details including `id` and `description`.
""",
)
async def get_all_zones(db: Session = Depends(get_db)) -> GetZonesResponse:
    zones = get_zones(db=db)

    return GetZonesResponse(total_count=len(zones), zones=zones)
