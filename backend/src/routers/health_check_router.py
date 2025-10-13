import socket
from datetime import datetime

from fastapi import APIRouter, status
from starlette.responses import JSONResponse

health_check_router = APIRouter(tags=["HealthCheck"], prefix="/health")


@health_check_router.get(
    "",
    response_model=str,
    status_code=status.HTTP_200_OK,
    summary="Health Check Endpoint",
    description="Returns the health status of the API along status metadata.",
)
async def health_check() -> JSONResponse:
    hostname = socket.gethostname()
    ip_address = socket.gethostbyname(hostname)
    time_stamp = datetime.now().isoformat()

    return JSONResponse(
        content={
            "status": "healthy",
            "success": True,
            "time_stamp": time_stamp,
            "service": "API",
        },
        headers={"hostname": hostname, "ip_address": ip_address},
    )
