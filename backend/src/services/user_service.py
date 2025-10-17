from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.core.security import hash_password
from src.errors.errors import ConflictException, UnprocessableEntityException
from src.models.db_models import User
from src.models.enums.user_role import UserRole
from src.schemas.user_schema import UserCreateRequest
from src.services.zone_service import get_random_zone


def get_user_by_email(*, db: Session, email: str) -> User | None:
    return db.query(User).filter_by(email=email).first()


def get_user_by_doi(*, db: Session, doi: str) -> User | None:
    return db.query(User).filter_by(doi=doi).first()


def create_user(*, db: Session, user_create_request: UserCreateRequest) -> User:
    if user_create_request.role not in {UserRole.INSTITUTIONAL, UserRole.COMMERCIAL}:
        raise UnprocessableEntityException("Role must be either 'institutional' or 'commercial'")

    existing_user = get_user_by_email(
        db=db, email=user_create_request.email
    ) or get_user_by_doi(db=db, doi=user_create_request.doi)
    if existing_user:
        raise ConflictException("User with this email or DOI already exists")

    user = User(
        full_name=user_create_request.full_name,
        email=user_create_request.email,
        hashed_password=hash_password(user_create_request.password),
        phone=user_create_request.phone,
        role=user_create_request.role,
        doi=user_create_request.doi,
        address=user_create_request.address,
        zone_id=get_random_zone(db=db).id if user_create_request.role == UserRole.COMMERCIAL else None,
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    logger.info(
        f"User created successfully with id [{user.id}] and email [{user.email}]"
    )

    return user
