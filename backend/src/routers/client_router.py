from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.models.db_models import User
from src.models.enums.user_role import UserRole
from src.schemas.client_schema import GetClientsResponse
from src.services.seller_service import get_clients_by_seller_id

client_router = APIRouter(
    tags=["Clients"],
    prefix="/clients",
)


@client_router.get(
    "",
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
