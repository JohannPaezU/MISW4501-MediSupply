from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.models.enums.user_role import UserRole
from src.schemas.distribution_center_schema import GetDistributionCentersResponse
from src.services.distribution_center_service import get_distribution_centers

distribution_center_router = APIRouter(
    tags=["Distribution Centers"],
    prefix="/distribution-centers",
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN, UserRole.COMMERCIAL]))],
)


@distribution_center_router.get(
    "",
    response_model=GetDistributionCentersResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all distribution centers",
    description="""
Retrieve a list of all distribution centers.

### Response
Returns a list of distribution centers with their details including `id`, `name`, `address`, `city`,
`country`, and `created_at`.
""",
)
async def get_all_distribution_centers(db: Session = Depends(get_db)) -> GetDistributionCentersResponse:
    distribution_centers = get_distribution_centers(db=db)

    return GetDistributionCentersResponse(total_count=len(distribution_centers),
                                          distribution_centers=distribution_centers)
