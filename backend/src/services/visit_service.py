from datetime import date, datetime, timezone

from fastapi import UploadFile
from google.cloud import storage
from sqlalchemy.orm import Session

from src.core.logging_config import logger
from src.errors.errors import BadRequestException, NotFoundException, ApiError
from src.models.db_models import User, Visit
from src.models.enums.user_role import UserRole
from src.models.enums.visit_status import VisitStatus
from src.schemas.visit_schema import VisitCreateRequest, VisitReportRequest
from src.services.geolocation_service import create_geolocation, create_geolocation_with_coordinates
from src.services.storage_service import upload_to_gcs


def create_visit(
    *,
    db: Session,
    visit_create_request: VisitCreateRequest,
    current_user: User,
) -> Visit:
    if visit_create_request.expected_date < date.today():
        raise BadRequestException("Expected date cannot be in the past")

    geolocation = create_geolocation(db=db, address=visit_create_request.address) if visit_create_request.address else current_user.geolocation
    visit = Visit(
        expected_date=visit_create_request.expected_date,
        expected_geolocation_id=geolocation.id,
        client_id=current_user.id,
        seller_id=current_user.seller_id,
    )

    db.add(visit)
    db.commit()
    db.refresh(visit)

    return visit


def get_all_visits(*, db: Session, current_user: User, expected_date: date | None = None,
                   visit_status: VisitStatus | None = None) -> list[Visit]:
    query = db.query(Visit)
    if current_user.role == UserRole.COMMERCIAL:
        query = query.filter_by(seller_id=current_user.id)
    else:
        query = query.filter_by(client_id=current_user.id)
    if expected_date:
        query = query.filter_by(expected_date=expected_date)
    if visit_status:
        query = query.filter_by(status=visit_status)

    return query.all()  # type: ignore


def get_visit_by_id(*, db: Session, current_user: User, visit_id: str) -> Visit | None:
    query = db.query(Visit).filter_by(id=visit_id)
    if current_user.role == UserRole.COMMERCIAL:
        query = query.filter_by(seller_id=current_user.id)
    else:
        query = query.filter_by(client_id=current_user.id)

    return query.first()


def report_visit(*, db: Session, visit_report_request: VisitReportRequest, current_user: User,
                 storage_client: storage.Client, visual_evidence: UploadFile | None = None) -> Visit:
    try:
        visit = get_visit_by_id(db=db, current_user=current_user, visit_id=visit_report_request.visit_id)
        if not visit or visit.status == VisitStatus.COMPLETED:
            raise NotFoundException("Visit not found or already reported")
        visit_date = visit_report_request.visit_date or datetime.now(timezone.utc)
        if visit_date.date() < visit.expected_date:
            raise BadRequestException("Visit date cannot be before expected date")
        _validate_visual_evidence(visual_evidence=visual_evidence)

        geolocation = create_geolocation_with_coordinates(db=db, latitude=visit_report_request.latitude, longitude=visit_report_request.longitude)
        visit.visit_date = visit_date
        visit.observations = visit_report_request.observations
        visit.report_geolocation_id = geolocation.id
        visit.status = VisitStatus.COMPLETED
        if visual_evidence:
            ext = visual_evidence.filename.split(".")[-1]
            file_path = f"visits/{visit.seller_id}/{visit.id}.{ext}"
            upload_to_gcs(storage_client=storage_client, file=visual_evidence, file_path=file_path)
            visit.visual_evidence_path = file_path

        db.commit()
        db.refresh(visit)

        return visit

    except ApiError:
        raise
    except Exception as e:
        logger.error(f"Error reporting visit: {str(e)}")
        raise ApiError("An error occurred while reporting the visit")


def _validate_visual_evidence(visual_evidence: UploadFile | None = None) -> None:  # pragma: no cover
    if not visual_evidence:
        return
    allowed_image_formats = {"jpg", "jpeg", "png", "bmp"}
    allowed_video_formats = {"mp4", "avi", "mov", "mkv"}
    allowed_file_formats = allowed_image_formats.union(allowed_video_formats)
    max_file_size_mb = 30
    file_format = visual_evidence.filename.split(".")[-1].lower()
    if file_format not in allowed_file_formats:
        raise BadRequestException(f"Invalid visual evidence file format. Allowed formats: {', '.join(allowed_file_formats)}")
    visual_evidence.file.seek(0, 2)
    file_size_mb = visual_evidence.file.tell() / (1024 * 1024)
    visual_evidence.file.seek(0)
    if file_size_mb > max_file_size_mb:
        raise BadRequestException(f"Visual evidence file size exceeds the maximum limit of {max_file_size_mb} MB")
