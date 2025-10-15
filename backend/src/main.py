from dotenv import load_dotenv
from fastapi import FastAPI

from src.core.logging_config import logger
from src.db.database import init_database
from src.errors.exception_handlers import setup_exception_handlers
from src.routers.auth_router import auth_router
from src.routers.health_check_router import health_check_router

version = "1.0"
prefix = f"/api/v{version.split('.')[0]}"
app = FastAPI(title="MediSupply API", version=version)

app.include_router(health_check_router, prefix=prefix)
app.include_router(auth_router, prefix=prefix)
setup_exception_handlers(app)


@app.on_event("startup")
def startup_event():  # pragma: no cover
    logger.info("Starting up the application...")
    load_dotenv(override=True)
    init_database()
    logger.info("Application startup complete")
