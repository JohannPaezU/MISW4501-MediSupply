from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.errors.errors import UnprocessableEntityException, ConflictException
from src.models.db_models import SellingPlan
from src.schemas.selling_plan_schema import SellingPlanCreateRequest
from src.services.product_service import get_product_by_id
from src.services.seller_service import get_seller_by_id
from src.services.zone_service import get_zone_by_id


def create_selling_plan(
    *, db: Session, selling_plan_create_request: SellingPlanCreateRequest
) -> SellingPlan:
    _validate_selling_plan_request(db=db, selling_plan_create_request=selling_plan_create_request)

    selling_plan = SellingPlan(
        period=selling_plan_create_request.period,
        goal=selling_plan_create_request.goal,
        product_id=selling_plan_create_request.product_id,
        zone_id=selling_plan_create_request.zone_id,
        seller_id=selling_plan_create_request.seller_id,
    )

    db.add(selling_plan)
    db.commit()
    db.refresh(selling_plan)
    logger.info(
        f"SellingPlan created successfully with ID: {selling_plan.id}"
    )

    return selling_plan


def get_selling_plans(*, db: Session) -> list[SellingPlan]:
    return db.query(SellingPlan).all()  # type: ignore


def get_selling_plan_by_id(
    *, db: Session, selling_plan_id: str
) -> SellingPlan | None:
    return db.query(SellingPlan).filter_by(id=selling_plan_id).first()


def _validate_selling_plan_request(*, db: Session, selling_plan_create_request: SellingPlanCreateRequest) -> None:
    existing_selling_plan = db.query(SellingPlan).filter_by(
        period=selling_plan_create_request.period,
        product_id=selling_plan_create_request.product_id,
        zone_id=selling_plan_create_request.zone_id,
        seller_id=selling_plan_create_request.seller_id,
    ).first()
    if existing_selling_plan:
        raise ConflictException("A selling plan with the same period, product, zone, and seller already exists")

    existing_product = get_product_by_id(db=db, product_id=selling_plan_create_request.product_id)
    if not existing_product:
        raise UnprocessableEntityException("Product with the given ID does not exist")

    existing_zone = get_zone_by_id(db=db, zone_id=selling_plan_create_request.zone_id)
    if not existing_zone:
        raise UnprocessableEntityException("Zone with the given ID does not exist")

    existing_seller = get_seller_by_id(db=db, seller_id=selling_plan_create_request.seller_id)
    if not existing_seller:
        raise UnprocessableEntityException("Seller with the given ID does not exist")