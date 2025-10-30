from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from starlette import status
from starlette.responses import JSONResponse

from src.core.logging_config import logger
from src.errors.errors import ApiError


def setup_exception_handlers(app: FastAPI) -> None:
    @app.exception_handler(ApiError)
    async def api_error_handler(_, error: ApiError) -> JSONResponse:
        logger.exception(f"API error occurred: {error}")

        return JSONResponse(
            status_code=error.status_code,
            content={
                "message": error.message,
            },
        )

    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(_, error: RequestValidationError) -> JSONResponse:
        logger.exception(f"Validation error: {error}")

        return JSONResponse(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            content={
                "message": "Validation Error",
                "detail": error.errors(),
            },
        )

    @app.exception_handler(Exception)
    async def generic_exception_handler(
        _, error: Exception
    ) -> JSONResponse:  # pragma: no cover
        logger.exception(f"Unexpected error: {error}")

        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content={
                "message": "Internal Server Error",
            },
        )
