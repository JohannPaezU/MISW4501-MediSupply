from sqlalchemy.orm import Session

from src.models.db_models import DistributionCenter


def get_distribution_centers(*, db: Session) -> list[DistributionCenter]:
    return db.query(DistributionCenter).all()  # type: ignore


def get_distribution_center_by_id(*, db: Session, distribution_center_id: str) -> DistributionCenter | None:
    return db.query(DistributionCenter).filter_by(id=distribution_center_id).first()
