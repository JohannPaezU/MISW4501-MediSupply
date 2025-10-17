from fastapi import APIRouter, status, Depends
from requests import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.enums.user_role import UserRole
from src.schemas.product_schema import ProductCreateResponse, ProductCreateRequest, GetProductsResponse, \
    ProductBase, ProductCreateBulkRequest, ProductCreateBulkResponse
from src.services.product_service import create_product, get_products, get_product_by_id, create_products_bulk

product_router = APIRouter(tags=["Products"], prefix="/products",
                           dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))])


@product_router.post(
    "",
    response_model=ProductCreateResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register a new product",
    description="""
Register a new product in the system.

### Request Body
- **name**: Name of the product (3-100 characters)
- **details**: Details about the product (10-500 characters)
- **store**: Store where the product is available (3-100 characters)
- **batch**: Batch identifier of the product (5-50 characters)
- **image_url**: URL of the product's image (optional, max 300 characters)
- **due_date**: Due date of the product (datetime)
- **stock**: Stock quantity of the product (greater than 0)
- **price_per_unite**: Price per unit of the product (greater than 0)
- **provider_id**: ID of the provider supplying the product (36 characters)

### Response
Returns the details of the newly created product including `id`, `name`, `details`, `store`, `batch`, `image_url`, `due_date`, `stock`, `price_per_unite`, `provider_id`, and `created_at`.
    
"""
)
async def register_product(
        *,
        product_create_request: ProductCreateRequest,
        db: Session = Depends(get_db),
) -> ProductCreateResponse:
    return create_product(db=db, product_create_request=product_create_request)


@product_router.post(
    "-batch",
    response_model=ProductCreateBulkResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register multiple products in bulk",
    description="""
Register multiple products in the system in a single request.

### Request Body
- **products**: A list of products to be created. Each product includes: 
    - **name**: Name of the product (3-100 characters)
    - **details**: Details about the product (10-500 characters)
    - **store**: Store where the product is available (3-100 characters)
    - **batch**: Batch identifier of the product (5-50 characters)
    - **image_url**: URL of the product's image (optional, max 300 characters)
    - **due_date**: Due date of the product (datetime)
    - **stock**: Stock quantity of the product (greater than 0)
    - **price_per_unite**: Price per unit of the product (greater than 0)
    - **provider_id**: ID of the provider supplying the product (36 characters)

### Response
- **success**: Indicates if the bulk operation was successful.
- **rows_total**: Total number of products attempted to be created.
- **rows_inserted**: Number of products successfully created.
- **errors**: Number of errors encountered during the operation.
- **errors_details**: A list of error messages for any products that failed to be created.
""",
)
async def register_products_bulk(
        *,
        product_create_bulk_request: ProductCreateBulkRequest,
        db: Session = Depends(get_db),
) -> ProductCreateBulkResponse:
    return create_products_bulk(db=db, product_create_bulk_request=product_create_bulk_request)


@product_router.get(
    "",
    response_model=GetProductsResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all products",
    description="""
Retrieve a list of all products in the system.

### Response
Returns a list of products along with the total count. Each product includes `id`, `name`, `details`, `store`, `batch`, `image_url`, `due_date`, `stock`, `price_per_unite`, `provider_id`, and `created_at`.
""",
)
async def get_all_products(
        *,
        db: Session = Depends(get_db),
) -> GetProductsResponse:
    products = get_products(db=db)

    return GetProductsResponse(total_count=len(products), products=products)


@product_router.get(
    "/{product_id}",
    response_model=ProductBase,
    status_code=status.HTTP_200_OK,
    summary="Get product by ID",
    description="""
Retrieve a product's details by their unique ID.

### Path Parameter
- **product_id**: The unique identifier of the product (36 characters)

### Response
Returns the details of the product including `id`, `name`, `details`, `store`, `batch`, `image_url`, `due_date`, `stock`, `price_per_unite`, `provider_id`, and `created_at`.
""",
)
async def get_product(
        *,
        product_id: str,
        db: Session = Depends(get_db),
) -> ProductBase:
    product = get_product_by_id(db=db, product_id=product_id)
    if not product:
        raise NotFoundException("Product not found")

    return product
