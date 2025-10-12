from fastapi import FastAPI

from src.routers.health_check_router import health_check_router

version = "1.0"
prefix = f"/api/v{version.split('.')[0]}"
app = FastAPI(
    title="MediSupply API",
    version=version
)

app.include_router(health_check_router, prefix=prefix)
