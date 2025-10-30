from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from src.core.config import settings
from src.core.logging_config import logger
from src.db.database import init_database
from src.errors.exception_handlers import setup_exception_handlers
from src.routers.auth_router import auth_router
from src.routers.client_router import client_router
from src.routers.distribution_center_router import distribution_center_router
from src.routers.health_check_router import health_check_router
from src.routers.order_router import order_router
from src.routers.product_router import product_router
from src.routers.provider_router import provider_router
from src.routers.seller_router import seller_router
from src.routers.selling_plan_router import selling_plan_router
from src.routers.visit_router import visit_router
from src.routers.zone_router import zone_router

version = "1.0"
prefix = f"/api/v{version.split('.')[0]}"
app = FastAPI(title="MediSupply API", version=version)

app.add_middleware(
    CORSMiddleware,  # type: ignore
    allow_origins=[
        origin.strip() for origin in settings.cors_origins.split(",") if origin.strip()
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(health_check_router, prefix=prefix)
app.include_router(auth_router, prefix=prefix)
app.include_router(zone_router, prefix=prefix)
app.include_router(seller_router, prefix=prefix)
app.include_router(provider_router, prefix=prefix)
app.include_router(product_router, prefix=prefix)
app.include_router(selling_plan_router, prefix=prefix)
app.include_router(distribution_center_router, prefix=prefix)
app.include_router(order_router, prefix=prefix)
app.include_router(client_router, prefix=prefix)
app.include_router(visit_router, prefix=prefix)
setup_exception_handlers(app)


@app.on_event("startup")
def startup_event():  # pragma: no cover
    logger.info("Starting up the application...")
    load_dotenv(override=True)
    init_database()
    logger.info("Application startup complete")
