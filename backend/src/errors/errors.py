class ApiError(Exception):
    status_code = 500
    message = "Internal Server Error"

    def __init__(self, message: str):
        self.message = message


class BadRequestException(ApiError):
    status_code = 400


class UnauthorizedException(ApiError):
    status_code = 401


class ConflictException(ApiError):
    status_code = 409
