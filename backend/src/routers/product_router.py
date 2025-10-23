from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.errors.errors import NotFoundException
from src.models.db_models import Product
from src.models.enums.user_role import UserRole
from src.schemas.product_schema import (
    ProductResponse,
    GetProductsResponse,
    ProductCreateBulkRequest,
    ProductCreateBulkResponse,
    ProductCreateRequest, OrderProductDetail
)
from src.services.product_service import (
    create_product,
    create_products_bulk,
    get_product_by_id,
    get_products,
)

product_router = APIRouter(
    tags=["Products"],
    prefix="/products",
    dependencies=[Depends(require_roles(allowed_roles=[UserRole.ADMIN]))],
)


@product_router.post(
    "",
    response_model=ProductResponse,
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
- **price_per_unit**: Price per unit of the product (greater than 0)
- **provider_id**: ID of the provider supplying the product (36 characters)

### Response
- **id**: Unique product ID
- **name**: Name of the product
- **details**: Details about the product
- **store**: Store where the product is available
- **batch**: Batch identifier of the product
- **image_url**: URL of the product's image
- **due_date**: Due date of the product
- **stock**: Stock quantity of the product
- **price_per_unit**: Price per unit of the product
- **created_at**: Timestamp of product creation
- **provider**: Associated provider details
- **selling_plans**: List of associated selling plans
- **order_products**: List of associated order products
""",
)
async def register_product(
        *,
        product_create_request: ProductCreateRequest,
        db: Session = Depends(get_db),
) -> ProductResponse:
    product = create_product(db=db, product_create_request=product_create_request)

    return _build_product_response(product=product)


@product_router.post(
    "-batch",
    response_model=ProductCreateBulkResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Register multiple products in bulk",
    description="""
Register multiple products in the system in a single request.

### Request Body
**products**: A list of products to be created. Each product includes:
- **name**: Name of the product (3-100 characters)
- **details**: Details about the product (10-500 characters)
- **store**: Store where the product is available (3-100 characters)
- **batch**: Batch identifier of the product (5-50 characters)
- **image_url**: URL of the product's image (optional, max 300 characters)
- **due_date**: Due date of the product (datetime)
- **stock**: Stock quantity of the product (greater than 0)
- **price_per_unit**: Price per unit of the product (greater than 0)
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
    return create_products_bulk(
        db=db, product_create_bulk_request=product_create_bulk_request
    )


@product_router.get(
    "",
    response_model=GetProductsResponse,
    status_code=status.HTTP_200_OK,
    summary="Get all products",
    description="""
Retrieve a list of all products in the system.

### Response
Returns a list of products with the following details for each product:
- **id**: Unique identifier of the product.
- **name**: Name of the product.
- **details**: Details about the product.
- **store**: Store where the product is available.
- **batch**: Batch identifier of the product.
- **image_url**: URL of the product's image.
- **due_date**: Due date of the product.
- **stock**: Stock quantity of the product.
- **price_per_unit**: Price per unit of the product.
- **created_at**: Timestamp when the product was created.
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
    response_model=ProductResponse,
    status_code=status.HTTP_200_OK,
    summary="Get product by ID",
    description="""
Retrieve a product's details by their unique ID.

### Path Parameter
- **product_id**: The unique identifier of the product (36 characters)

### Response
- **id**: Unique identifier of the product.
- **name**: Name of the product.
- **details**: Details about the product.
- **store**: Store where the product is available.
- **batch**: Batch identifier of the product.
- **image_url**: URL of the product's image.
- **due_date**: Due date of the product.
- **stock**: Stock quantity of the product.
- **price_per_unit**: Price per unit of the product.
- **created_at**: Timestamp when the product was created.
- **provider**: Associated provider details.
- **selling_plans**: List of associated selling plans.
- **order_products**: List of associated order products.
""",
)
async def get_product(
        *,
        product_id: str,
        db: Session = Depends(get_db),
) -> ProductResponse:
    product = get_product_by_id(db=db, product_id=product_id)
    if not product:
        raise NotFoundException("Product not found")

    return _build_product_response(product=product)


def _build_product_response(product: Product) -> ProductResponse:
    return ProductResponse(
        **product.__dict__,
        provider=product.provider,
        selling_plans=product.selling_plans,
        orders=[OrderProductDetail.from_order_product(order_product=order_product) for order_product in
                product.order_products]
    )
