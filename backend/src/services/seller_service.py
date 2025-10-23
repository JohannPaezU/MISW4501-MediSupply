import random
import string

from sqlalchemy import func
from sqlalchemy.orm import Session

from src.core.config import settings
from src.core.logging_config import logger
from src.core.security import hash_password
from src.core.utils import get_template_path
from src.errors.errors import ConflictException, UnprocessableEntityException
from src.models.db_models import User
from src.models.enums.user_role import UserRole
from src.schemas.seller_schema import SellerCreateRequest
from src.services.email_service import send_email
from src.services.requests.email_request import EmailRequest
from src.services.user_service import get_user_by_doi, get_user_by_email
from src.services.zone_service import get_zone_by_id


def create_seller(*, db: Session, seller_create_request: SellerCreateRequest) -> User:
    existing_user = get_user_by_email(
        db=db, email=seller_create_request.email
    ) or get_user_by_doi(db=db, doi=seller_create_request.doi)
    if existing_user:
        raise ConflictException("Seller with this email or DOI already exists")

    existing_zone = get_zone_by_id(db=db, zone_id=seller_create_request.zone_id)
    if not existing_zone:
        raise UnprocessableEntityException("Zone with the given ID does not exist")

    temporary_password = _generate_temporary_password()
    user = User(
        full_name=seller_create_request.full_name,
        email=seller_create_request.email,
        hashed_password=hash_password(temporary_password),
        phone=seller_create_request.phone,
        role=UserRole.COMMERCIAL,
        doi=seller_create_request.doi,
        zone_id=seller_create_request.zone_id,
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    logger.info(
        f"Seller (UserRole.COMMERCIAL) created successfully with id [{user.id}] and email [{user.email}]"
    )
    _send_temporary_password_email(user=user, temporary_password=temporary_password)

    return user


def get_sellers(*, db: Session) -> list[User]:
    return db.query(User).filter_by(role=UserRole.COMMERCIAL).all()  # type: ignore


def get_seller_by_id(*, db: Session, seller_id: str) -> User | None:
    return db.query(User).filter_by(id=seller_id, role=UserRole.COMMERCIAL).first()


def get_random_seller(*, db: Session) -> User | None:
    return db.query(User).filter_by(role=UserRole.COMMERCIAL).order_by(func.random()).first()


def get_clients_by_seller_id(*, db: Session, seller_id: str) -> list[User]:
    seller = get_seller_by_id(db=db, seller_id=seller_id)

    return seller.clients


def get_institutional_client_for_seller(*, db: Session, seller_id: str, client_id: str) -> User | None:
    return db.query(User).filter_by(id=client_id, role=UserRole.INSTITUTIONAL, seller_id=seller_id).first()


def _generate_temporary_password() -> str:
    length = 8
    safe_symbols = "@#$-_!"
    characters = string.ascii_letters + string.digits + safe_symbols
    temporary_password = "".join(random.choice(characters) for _ in range(length))

    return temporary_password


def _send_temporary_password_email(user: User, temporary_password: str) -> None:
    html_template = open(
        get_template_path("temporary_password_template.html"), encoding="utf-8"
    ).read()
    email_request = EmailRequest.from_template(
        html_template=html_template,
        email_receiver=user.email,
        email_subject="¡Bienvenido a MediSupply! - Tu contraseña temporal",
        template_values={
            "full_name": user.full_name,
            "temporary_password": temporary_password,
            "email": user.email,
            "login_url": settings.login_url,
        },
    )
    send_email(email_request)
