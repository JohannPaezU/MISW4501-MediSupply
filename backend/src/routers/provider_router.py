from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.enums.user_role import UserRole
from src.schemas.provider_schema import (
    GetProvidersResponse,
    GetProviderResponse,
    ProviderCreateRequest,
    ProviderCreateResponse,
)
from src.services.provider_service import (
    create_provider,
    get_provider_by_id,
    get_providers,
)

provider_router = APIRouter(
    tags=["Providers"],
    prefix="/providers",
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
)


@provider_router.post(
    "",
    response_model=ProviderCreateResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register a new provider",
    description="""
Register a new provider in the system.

### Request Body
- **name**: Full name of the provider (1-100 characters)
- **rit**: RIT of the provider (1-50 characters)
- **city**: City where the provider is located (1-100 characters)
- **country**: Country where the provider is located (1-100 characters)
- **image_url**: URL of the provider's image (optional, max 255 characters)
- **email**: Email address of the provider (valid email format, max 120 characters)
- **phone**: Phone number of the provider (9-15 digits)

### Response
Returns the details of the newly created provider including `id`, `name`, `rit`, `city`, `country`, `image_url`,
`email`, `phone`, and `created_at`.
""",
)
async def register_provider(
    *,
    provider_create_request: ProviderCreateRequest,
    db: Session = Depends(get_db),
) -> ProviderCreateResponse:
    return create_provider(db=db, provider_create_request=provider_create_request)


@provider_router.get(
    "",
    response_model=GetProvidersResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all providers",
    description="""
Retrieve a list of all providers in the system.

### Response
Returns a list of providers along with the total count. Each provider includes `id`, `name`, `rit`, `city`, `country`,
`image_url`, `email`, `phone`, and `created_at`.
""",
)
async def get_all_providers(
    *,
    db: Session = Depends(get_db),
) -> GetProvidersResponse:
    providers = get_providers(db=db)

    return GetProvidersResponse(total_count=len(providers), providers=providers)


@provider_router.get(
    "/{provider_id}",
    response_model=GetProviderResponse,
    status_code=status.HTTP_200_OK,
    summary="Get provider by ID",
    description="""
Retrieve a provider's details by their unique ID.

### Path Parameter
- **provider_id**: The unique identifier of the provider (36 characters)

### Response
Returns the details of the provider including `id`, `name`, `rit`, `city`, `country`, `image_url`, `email`,
`phone`, and `created_at`.
""",
)
async def get_provider(
    *,
    provider_id: str,
    db: Session = Depends(get_db),
) -> GetProviderResponse:
    provider = get_provider_by_id(db=db, provider_id=provider_id)
    if not provider:
        raise NotFoundException("Provider not found")

    return provider
