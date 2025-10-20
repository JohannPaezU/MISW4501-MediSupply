from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.enums.user_role import UserRole
from src.schemas.seller_schema import (
    GetSellerResponse,
    GetSellersResponse,
    SellerCreateRequest,
    SellerCreateResponse,
)
from src.services.seller_service import create_seller, get_seller_by_id, get_sellers

seller_router = APIRouter(
    tags=["Sellers"],
    prefix="/sellers",
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
)


@seller_router.post(
    "",
    response_model=SellerCreateResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register a new seller",
    description="""
Create a new seller account.
### Request Body
- **full_name**: Seller's full name (1–100 characters)
- **doi**: Unique identification number (1–50 characters)
- **email**: Seller's email address (5–120 characters)
- **phone**: Phone number (9–15 digits)
- **zone_id**: Zone ID (36 characters)

### Response
Returns the created seller's details including `id`, `full_name`, `doi`, `email`, `phone`, `created_at`
and associated `zone` information.
""",
)
async def register_seller(
    *,
    seller_create_request: SellerCreateRequest,
    db: Session = Depends(get_db),
) -> SellerCreateResponse:
    return create_seller(db=db, seller_create_request=seller_create_request)


@seller_router.get(
    "",
    response_model=GetSellersResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all sellers",
    description="""
Retrieve a list of all registered sellers.

### Response
Returns a list of sellers with their details including `id`, `full_name`, `doi`, `email`, `phone`, `created_at`
and associated `zone` information.
""",
)
async def get_all_sellers(
    *,
    db: Session = Depends(get_db),
) -> GetSellersResponse:
    sellers = get_sellers(db=db)

    return GetSellersResponse(total_count=len(sellers), sellers=sellers)


@seller_router.get(
    "/{seller_id}",
    response_model=GetSellerResponse,
    status_code=status.HTTP_200_OK,
    summary="Get seller by ID",
    description="""
Retrieve a seller's details by their unique ID.

### Path Parameters
- **seller_id**: The unique identifier of the seller (36 characters)

### Response
Returns the seller's details including `id`, `full_name`, `doi`, `email`, `phone`, `created_at`
and associated `zone` information.
""",
)
async def get_seller(
    *,
    seller_id: str,
    db: Session = Depends(get_db),
) -> GetSellerResponse:
    seller = get_seller_by_id(db=db, seller_id=seller_id)
    if not seller:
        raise NotFoundException("Seller not found")

    return seller
