from sqlalchemy import func
from sqlalchemy.orm import Session

from src.models.db_models import Zone


def get_zones(*, db: Session) -> list[Zone]:
    return db.query(Zone).all()  # type: ignore


def get_zone_by_id(*, db: Session, zone_id: str) -> Zone | None:
    return db.query(Zone).filter_by(id=zone_id).first()


def get_random_zone(*, db: Session) -> Zone | None:
    return db.query(Zone).order_by(func.random()).first()
