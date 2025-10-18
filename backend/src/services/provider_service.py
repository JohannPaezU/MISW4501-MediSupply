from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.errors.errors import ConflictException
from src.models.db_models import Provider
from src.schemas.provider_schema import ProviderCreateRequest


def create_provider(
    *, db: Session, provider_create_request: ProviderCreateRequest
) -> Provider:
    existing_provider = get_provider_by_email(
        db=db, email=provider_create_request.email
    ) or get_provider_by_rit(db=db, rit=provider_create_request.rit)
    if existing_provider:
        raise ConflictException("Provider with this email or RIT already exists")

    provider = Provider(
        name=provider_create_request.name,
        rit=provider_create_request.rit,
        city=provider_create_request.city,
        country=provider_create_request.country,
        image_url=provider_create_request.image_url,
        email=provider_create_request.email,
        phone=provider_create_request.phone,
    )

    db.add(provider)
    db.commit()
    db.refresh(provider)
    logger.info(
        f"Provider created successfully with id [{provider.id}] and email [{provider.email}]"
    )

    return provider


def get_providers(*, db: Session) -> list[Provider]:
    return db.query(Provider).all()  # type: ignore


def get_provider_by_id(
    *, db: Session, provider_id: str
) -> Provider | None:
    return db.query(Provider).filter_by(id=provider_id).first()


def get_provider_by_email(
    *, db: Session, email: str
) -> Provider | None:
    return db.query(Provider).filter_by(email=email).first()


def get_provider_by_rit(
    *, db: Session, rit: str
) -> Provider | None:
    return db.query(Provider).filter_by(rit=rit).first()
