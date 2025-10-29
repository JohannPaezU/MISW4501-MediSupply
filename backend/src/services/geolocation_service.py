from sqlalchemy.orm import Session
from src.models.db_models import Geolocation
from src.services.geocoding_service import get_validated_address


def create_geolocation(*, db: Session, address: str) -> Geolocation:
    validated_address = get_validated_address(address=address)

    geolocation = Geolocation(
        address=validated_address.formatted_address,
        latitude=validated_address.latitude,
        longitude=validated_address.longitude,
    )
    db.add(geolocation)
    db.commit()
    db.refresh(geolocation)

    return geolocation
