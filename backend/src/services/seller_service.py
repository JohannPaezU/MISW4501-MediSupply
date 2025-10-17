import random
import string

from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.core.security import hash_password
from src.errors.errors import ConflictException, BadRequestException
from src.models.db_models import User
from src.models.enums.user_role import UserRole
from src.schemas.seller_schema import SellerCreateRequest
from src.services.user_service import get_user_by_email, get_user_by_doi
from src.services.zone_service import get_zone_by_id


def create_seller(*, db: Session, seller_create_request: SellerCreateRequest) -> User:
    existing_user = get_user_by_email(
        db=db, email=seller_create_request.email
    ) or get_user_by_doi(db=db, doi=seller_create_request.doi)
    if existing_user:
        raise ConflictException("User with this email or DOI already exists")

    existing_zone = get_zone_by_id(db=db, zone_id=seller_create_request.zone_id)
    if not existing_zone:
        raise BadRequestException("Zone with the given ID does not exist")

    user = User(
        full_name=seller_create_request.full_name,
        email=seller_create_request.email,
        hashed_password=hash_password(_generate_temporary_password()),
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

    return user


def get_sellers(*, db: Session) -> list[User]:
    return db.query(User).filter_by(role=UserRole.COMMERCIAL).all()  # type: ignore


def get_seller_by_id(*, db: Session, seller_id: str) -> User | None:
    return db.query(User).filter_by(id=seller_id, role=UserRole.COMMERCIAL).first()


def _generate_temporary_password() -> str:
    length = 8
    characters = string.ascii_letters + string.digits + string.punctuation
    temporary_password = ''.join(random.choice(characters) for i in range(length))

    return temporary_password
