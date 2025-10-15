from sqlalchemy.orm import Session
from src.errors.errors import UnauthorizedException
from src.services.otp_service import create_otp, verify_otp
from src.services.user_service import get_user_by_email
from src.services.email_service import send_email
from src.services.requests.email_request import EmailRequest
from src.schemas.auth_schema import LoginRequest, OTPVerifyRequest
from src.core.security import verify_password, create_access_token


def login_user(*, db: Session, login_request: LoginRequest) -> int:
    user = get_user_by_email(db=db, email=login_request.email)  # noqa
    if not user or not verify_password(login_request.password, user.hashed_password):
        raise UnauthorizedException("Invalid email or password")

    otp = create_otp(db=db, user=user)
    html_template = open("./src/templates/otp_template.html", encoding="utf-8").read()
    email_request = EmailRequest.from_template(
        html_template=html_template,
        email_receiver=user.email,
        email_subject="Tu código de verificación para MediSupply",
        template_values={"full_name": user.full_name, "otp_code": otp.code,
                         "otp_expiration_minutes": otp.expiration_minutes}
    )
    send_email(email_request)

    return otp.expiration_minutes


def verify_otp_and_get_token(*, db: Session, otp_verify_request: OTPVerifyRequest) -> str:
    user = get_user_by_email(db=db, email=otp_verify_request.email)
    if not user:
        raise UnauthorizedException("Invalid or expired OTP")
    verify_otp(db=db, user=user, otp_code=otp_verify_request.otp_code)
    token_data = {
        "sub": str(user.id),
        "role": user.role.value,
    }
    access_token = create_access_token(data=token_data)

    return access_token
