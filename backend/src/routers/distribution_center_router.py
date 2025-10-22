from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.enums.user_role import UserRole
from src.schemas.distribution_center_schema import GetDistributionCentersResponse, DistributionCenterResponse
from src.services.distribution_center_service import get_distribution_centers, get_distribution_center_by_id

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
Returns a list of distribution centers with the following details for each center:
- **id**: Unique identifier of the distribution center.
- **name**: Name of the distribution center.
- **address**: Address of the distribution center.
- **city**: City where the distribution center is located.
- **country**: Country where the distribution center is located.
- **created_at**: Timestamp when the distribution center was created.
""",
)
async def get_all_distribution_centers(db: Session = Depends(get_db)) -> GetDistributionCentersResponse:
    distribution_centers = get_distribution_centers(db=db)

    return GetDistributionCentersResponse(total_count=len(distribution_centers),
                                          distribution_centers=distribution_centers)


@distribution_center_router.get(
    "/{distribution_center_id}",
    response_model=DistributionCenterResponse,
    status_code=status.HTTP_200_OK,
    summary="Get distribution center by ID",
    description="""
Retrieve details of a specific distribution center by its ID.

### Path Parameters
- **distribution_center_id** (str): Unique identifier of the distribution center.

### Response
- **id**: Unique identifier of the distribution center.
- **name**: Name of the distribution center.
- **address**: Address of the distribution center.
- **city**: City where the distribution center is located.
- **country**: Country where the distribution center is located.
- **created_at**: Timestamp when the distribution center was created.
- **orders**: List of orders associated with the distribution center.
""",
)
async def get_distribution_center(
    distribution_center_id: str,
    db: Session = Depends(get_db),
) -> DistributionCenterResponse:
    distribution_center = get_distribution_center_by_id(db=db, distribution_center_id=distribution_center_id)
    if not distribution_center:
        raise NotFoundException("Distribution center not found")
    
    return distribution_center
