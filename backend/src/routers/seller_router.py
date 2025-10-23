from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.db_models import User
from src.models.enums.user_role import UserRole
from src.schemas.seller_schema import (
    GetSellersResponse,
    SellerCreateRequest,
    SellerResponse,
    GetClientsResponse,
)
from src.services.seller_service import create_seller, get_seller_by_id, get_sellers, get_clients_by_seller_id

seller_router = APIRouter(
    tags=["Sellers"],
    prefix="/sellers",
)


@seller_router.post(
    "",
    response_model=SellerResponse,
    status_code=status.HTTP_201_CREATED,
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
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
- **id**: Unique identifier of the seller
- **full_name**: Seller's full name
- **email**: Seller's email address
- **phone**: Seller's phone number
- **doi**: Seller's unique identification number
- **address**: Seller's address (if provided)
- **role**: User role (should be 'commercial')
- **created_at**: Timestamp of when the seller was created
- **zone**: Associated zone information
- **clients**: List of clients associated with the seller
- **selling_plans**: List of selling plans associated with the seller
- **managed_orders**: List of orders managed by the seller
""",
)
async def register_seller(
    *,
    seller_create_request: SellerCreateRequest,
    db: Session = Depends(get_db),
) -> SellerResponse:
    return create_seller(db=db, seller_create_request=seller_create_request)


@seller_router.get(
    "",
    response_model=GetSellersResponse,
    status_code=status.HTTP_200_OK,
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
    summary="Get all sellers",
    description="""
Retrieve a list of all registered sellers.

### Response
Returns a list of sellers with the following details for each seller:
- **id**: Unique identifier of the seller
- **full_name**: Seller's full name
- **email**: Seller's email address
- **phone**: Seller's phone number
- **doi**: Seller's unique identification number
- **address**: Seller's address (if provided)
- **role**: User role (should be 'commercial')
- **created_at**: Timestamp of when the seller was created
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
    response_model=SellerResponse,
    status_code=status.HTTP_200_OK,
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
    summary="Get seller by ID",
    description="""
Retrieve a seller's details by their unique ID.

### Path Parameters
- **seller_id**: The unique identifier of the seller (36 characters)

### Response
- **id**: Unique identifier of the seller
- **full_name**: Seller's full name
- **email**: Seller's email address
- **phone**: Seller's phone number
- **doi**: Seller's unique identification number
- **address**: Seller's address (if provided)
- **role**: User role (should be 'commercial')
- **created_at**: Timestamp of when the seller was created
- **zone**: Associated zone information
- **clients**: List of clients associated with the seller
- **selling_plans**: List of selling plans associated with the seller
- **managed_orders**: List of orders managed by the seller
""",
)
async def get_seller(
    *,
    seller_id: str,
    db: Session = Depends(get_db),
) -> SellerResponse:
    seller = get_seller_by_id(db=db, seller_id=seller_id)
    if not seller:
        raise NotFoundException("Seller not found")

    return seller


@seller_router.get(
    "/me/clients",
    response_model=GetClientsResponse,
    status_code=status.HTTP_200_OK,
    summary="Get clients for the current seller",
    description="""
Retrieve a list of clients associated with the currently authenticated seller.

### Response
Returns a list of clients of the seller with the following details for each client:
- **id**: Unique identifier of the client
- **full_name**: Client's full name
- **email**: Client's email address
- **phone**: Client's phone number
- **doi**: Client's unique identification number
- **address**: Client's address
- **role**: User role
- **created_at**: Timestamp of when the client was created
""",
)
async def get_clients(
    *,
    current_user: User = Depends(require_roles(allowed_roles=[UserRole.COMMERCIAL])),
    db: Session = Depends(get_db),
) -> GetClientsResponse:
    clients = get_clients_by_seller_id(db=db, seller_id=current_user.id)

    return GetClientsResponse(total_count=len(clients), clients=clients)
