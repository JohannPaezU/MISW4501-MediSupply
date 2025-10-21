from sqlalchemy.orm import Session

from src.models.db_models import DistributionCenter


def get_distribution_centers(*, db: Session) -> list[DistributionCenter]:
    return db.query(DistributionCenter).all()  # type: ignore


