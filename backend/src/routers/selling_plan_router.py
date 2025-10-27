from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.enums.user_role import UserRole
from src.schemas.selling_plan_schema import (
    GetSellingPlansResponse,
    SellingPlanCreateRequest,
    SellingPlanResponse,
)
from src.services.selling_plan_service import (
    create_selling_plan,
    get_selling_plan_by_id,
    get_selling_plans,
)

selling_plan_router = APIRouter(
    tags=["Selling Plans"],
    prefix="/selling-plans",
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
)


@selling_plan_router.post(
    "",
    response_model=SellingPlanResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Create a new selling plan",
    description="""
Create a new selling plan in the system.

### Request Body
- **period**: Period of the selling plan (1-20 characters)
- **goal**: Goal of the selling plan (positive integer)
- **product_id**: ID of the associated product (36 characters)
- **zone_id**: ID of the associated zone (36 characters)
- **seller_id**: ID of the associated seller (36 characters)

### Response
- **id**: Unique identifier of the selling plan
- **period**: Period of the selling plan
- **goal**: Goal of the selling plan
- **created_at**: Timestamp of when the selling plan was created
- **product**: Details of the associated product
- **zone**: Details of the associated zone
- **seller**: Details of the associated seller
""",
)
async def register_selling_plan(
    *,
    selling_plan_create_request: SellingPlanCreateRequest,
    db: Session = Depends(get_db),
) -> SellingPlanResponse:
    return create_selling_plan(
        db=db, selling_plan_create_request=selling_plan_create_request
    )


@selling_plan_router.get(
    "",
    response_model=GetSellingPlansResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all selling plans",
    description="""
Retrieve a list of all selling plans in the system.

### Response
Returns a list of selling plans with the following details for each selling plan:
- **id**: Unique identifier of the selling plan.
- **period**: Period of the selling plan.
- **goal**: Goal of the selling plan.
- **created_at**: Timestamp when the selling plan was created.
""",
)
async def get_all_selling_plans(
    *,
    db: Session = Depends(get_db),
) -> GetSellingPlansResponse:
    selling_plans = get_selling_plans(db=db)

    return GetSellingPlansResponse(
        total_count=len(selling_plans), selling_plans=selling_plans
    )


@selling_plan_router.get(
    "/{selling_plan_id}",
    response_model=SellingPlanResponse,
    status_code=status.HTTP_200_OK,
    summary="Get selling plan by ID",
    description="""
Retrieve a selling plan's details by its unique ID.

### Path Parameter
- **selling_plan_id**: The unique identifier of the selling plan (36 characters)

### Response
- **id**: Unique identifier of the selling plan.
- **period**: Period of the selling plan.
- **goal**: Goal of the selling plan.
- **created_at**: Timestamp when the selling plan was created.
- **product**: Details of the associated product.
- **zone**: Details of the associated zone.
- **seller**: Details of the associated seller.
""",
)
async def get_selling_plan(
    *,
    selling_plan_id: str,
    db: Session = Depends(get_db),
) -> SellingPlanResponse:
    selling_plan = get_selling_plan_by_id(db=db, selling_plan_id=selling_plan_id)
    if not selling_plan:
        raise NotFoundException("Selling plan not found")

    return selling_plan
