class ApiError(Exception):
    status_code = 500
    message = "Internal Server Error"

    def __init__(self, message: str):
        self.message = message


class BadRequestException(ApiError):
    status_code = 400


class UnauthorizedException(ApiError):
    status_code = 401


class ForbiddenException(ApiError):
    status_code = 403


class NotFoundException(ApiError):
    status_code = 404


class ConflictException(ApiError):
    status_code = 409
