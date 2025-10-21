from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.core.security import hash_password
from src.errors.errors import ConflictException, UnprocessableEntityException
from src.models.db_models import User
from src.models.enums.user_role import UserRole
from src.schemas.user_schema import UserCreateRequest


def get_user_by_email(*, db: Session, email: str) -> User | None:
    return db.query(User).filter_by(email=email).first()


def get_user_by_doi(*, db: Session, doi: str) -> User | None:
    return db.query(User).filter_by(doi=doi).first()


def create_user(*, db: Session, user_create_request: UserCreateRequest) -> User:
    from src.services.seller_service import get_random_seller  # Imported here to avoid circular dependency
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
        role=UserRole.INSTITUTIONAL,
        doi=user_create_request.doi,
        address=user_create_request.address,
        seller_id=get_random_seller(db=db).id
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    logger.info(
        f"User created successfully with id [{user.id}] and email [{user.email}]"
    )

    return user
