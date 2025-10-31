from datetime import date, datetime
from typing import Union

from fastapi import APIRouter, Depends, File, Form, Query, UploadFile, status
from google.cloud import storage
from sqlalchemy.orm import Session

from src.core.security import require_roles
from src.db.database import get_db
from src.dependencies.gcp_dependency import StorageClientSingleton
from src.errors.errors import NotFoundException
from src.models.db_models import User, Visit
from src.models.enums.user_role import UserRole
from src.models.enums.visit_status import VisitStatus
from src.schemas.visit_schema import (
    ClientVisitResponse,
    GetClientVisitsResponse,
    GetSellerVisitsResponse,
    SellerVisitResponse,
    VisitCreateRequest,
    VisitReportRequest,
    VisitResponse,
)
from src.services.storage_service import generate_signed_url
from src.services.visit_service import (
    create_visit,
    get_visit_by_id,
    get_visits,
    report_visit,
)

visit_router = APIRouter(tags=["Visits"], prefix="/visits")


@visit_router.post(
    "",
    response_model=VisitResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Request a new visit for institutional users",
    description="""
Create a new visit request in the system.

### Request Body
- **expected_date**: Date when the visit is expected to occur.
- **address**: (Optional) Address for the visit. If not provided, the user's registered address will be used.

### Response
- **id**: Unique identifier of the visit.
- **expected_date**: Date when the visit is expected to occur.
- **status**: Current status of the visit.
- **created_at**: Timestamp when the visit was created.
- **expected_geolocation**: Geolocation details for the expected visit location.
- **client**: Information about the client requesting the visit.
- **seller**: Information about the seller assigned to the visit.
""",
)
async def register_visit(
    *,
    visit_create_request: VisitCreateRequest,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_roles(allowed_roles=[UserRole.INSTITUTIONAL])),
) -> VisitResponse:
    return create_visit(
        db=db, visit_create_request=visit_create_request, current_user=current_user
    )


@visit_router.get(
    "",
    response_model=Union[GetSellerVisitsResponse, GetClientVisitsResponse],
    status_code=status.HTTP_200_OK,
    summary="Get all visits for the current user",
    description="""
Retrieve all visits scheduled or assigned to the current user.

### Query Parameters
- **expected_date**: (Optional) Filter visits by expected date (YYYY-MM-DD format).
- **visit_status**: (Optional) Filter visits by their status. Possible values are: 'pending' and 'completed'.

### Response
- **total_count**: Total number of visits matching the criteria.
- **visits**: List of visits with their basic information:
    - **id**: Unique identifier of the visit.
    - **expected_date**: Date when the visit is expected to occur.
    - **visit_date**: Actual date when the visit took place (if applicable).
    - **observations**: Observations made during the visit (if applicable).
    - **visual_evidence_url**: URL to visual evidence from the visit (if applicable).
    - **status**: Current status of the visit.
    - **created_at**: Timestamp when the visit was created.
    - **expected_geolocation**: Geolocation details for the expected visit location.
    - **report_geolocation**: Geolocation details for the reported visit location (if applicable).
    - **client**: Information about the client associated with the visit (for sellers).
    - **seller**: Information about the seller associated with the visit (for clients).
""",
)
async def get_all_visits(
    *,
    expected_date: date | None = Query(None),
    visit_status: VisitStatus | None = Query(None),
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(allowed_roles=[UserRole.COMMERCIAL, UserRole.INSTITUTIONAL])
    ),
    storage_client: storage.Client = Depends(StorageClientSingleton),
) -> Union[GetSellerVisitsResponse, GetClientVisitsResponse]:
    visits = get_visits(
        db=db,
        current_user=current_user,
        expected_date=expected_date,
        visit_status=visit_status,
    )
    visit_responses = _build_visit_responses(
        visits=visits, storage_client=storage_client
    )
    if current_user.role == UserRole.COMMERCIAL:
        return GetSellerVisitsResponse(
            total_count=len(visits),
            visits=[SellerVisitResponse.model_validate(v) for v in visit_responses],
        )

    return GetClientVisitsResponse(
        total_count=len(visits),
        visits=[ClientVisitResponse.model_validate(v) for v in visit_responses],
    )


@visit_router.get(
    "/{visit_id}",
    response_model=VisitResponse,
    status_code=status.HTTP_200_OK,
    summary="Get visit by ID for the current user",
    description="""
Retrieve detailed information about a specific visit by its ID for the current user.

### Path Parameters
- **visit_id**: Unique identifier of the visit (integer).

### Response
- **id**: Unique identifier of the visit.
- **expected_date**: Date when the visit is expected to occur.
- **visit_date**: Actual date when the visit took place (if applicable).
- **observations**: Observations made during the visit (if applicable).
- **visual_evidence_url**: URL to visual evidence from the visit (if applicable).
- **status**: Current status of the visit.
- **created_at**: Timestamp when the visit was created.
- **expected_geolocation**: Geolocation details for the expected visit location.
- **report_geolocation**: Geolocation details for the reported visit location (if applicable).
- **client**: Information about the client associated with the visit.
- **seller**: Information about the seller associated with the visit.
""",
)
async def get_visit(
    *,
    visit_id: str,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_roles(allowed_roles=[UserRole.COMMERCIAL, UserRole.INSTITUTIONAL])
    ),
    storage_client: storage.Client = Depends(StorageClientSingleton),
) -> VisitResponse:
    visit = get_visit_by_id(db=db, current_user=current_user, visit_id=visit_id)
    if not visit:
        raise NotFoundException("Visit not found")

    return _build_visit_response(visit=visit, storage_client=storage_client)


@visit_router.patch(
    "/{visit_id}/report",
    response_model=VisitResponse,
    status_code=status.HTTP_200_OK,
    summary="Report the outcome of a visit",
    description="""
Report the outcome of a scheduled visit by providing details such as visit date, observations,
file evidence, and geolocation.

### Path Parameters
- **visit_id**: Unique identifier of the visit (string).

### Form Data
- **visit_date**: Date and time when the visit took place.
- **observations**: (Optional) Observations made during the visit.
- **latitude**: Latitude of the visit location.
- **longitude**: Longitude of the visit location.
- **visual_evidence**: (Optional) File upload for visual evidence from the visit.

### Response
- **id**: Unique identifier of the visit.
- **expected_date**: Date when the visit is expected to occur.
- **visit_date**: Actual date when the visit took place (if applicable).
- **observations**: Observations made during the visit (if applicable).
- **visual_evidence_url**: URL to visual evidence from the visit (if applicable).
- **status**: Current status of the visit.
- **created_at**: Timestamp when the visit was created.
- **expected_geolocation**: Geolocation details for the expected visit location.
- **report_geolocation**: Geolocation details for the reported visit location (if applicable).
- **client**: Information about the client associated with the visit.
- **seller**: Information about the seller associated with the visit.
""",
)
async def report_visit_by_id(
    *,
    visit_id: str,
    visit_date: datetime | None = Form(None),
    observations: str | None = Form(None),
    latitude: float = Form(...),
    longitude: float = Form(...),
    visual_evidence: UploadFile | None = File(None),
    db: Session = Depends(get_db),
    current_user: User = Depends(require_roles(allowed_roles=[UserRole.COMMERCIAL])),
    storage_client: storage.Client = Depends(StorageClientSingleton),
) -> VisitResponse:
    visit_report_request = VisitReportRequest(
        visit_id=visit_id,
        visit_date=visit_date,
        observations=observations,
        latitude=latitude,
        longitude=longitude,
    )

    visit = report_visit(
        db=db,
        visit_report_request=visit_report_request,
        current_user=current_user,
        storage_client=storage_client,
        visual_evidence=visual_evidence,
    )

    return _build_visit_response(visit=visit, storage_client=storage_client)


def _build_visit_response(
    visit: Visit, storage_client: storage.Client
) -> VisitResponse:
    visual_evidence_url = None
    if visit.visual_evidence_path:
        visual_evidence_url = generate_signed_url(
            storage_client=storage_client, blob_name=str(visit.visual_evidence_path)
        )

    return VisitResponse(
        **visit.__dict__,
        visual_evidence_url=visual_evidence_url,
        expected_geolocation=visit.expected_geolocation,
        report_geolocation=visit.report_geolocation,
        client=visit.client,
        seller=visit.seller,
    )


def _build_visit_responses(
    visits: list[Visit], storage_client: storage.Client
) -> list[VisitResponse]:
    visit_responses = []
    for visit in visits:
        visual_evidence_url = None
        if visit.visual_evidence_path:
            visual_evidence_url = generate_signed_url(
                storage_client=storage_client, blob_name=str(visit.visual_evidence_path)
            )
        visit_response = VisitResponse(
            **visit.__dict__,
            visual_evidence_url=visual_evidence_url,
            expected_geolocation=visit.expected_geolocation,
            report_geolocation=visit.report_geolocation,
            client=visit.client,
            seller=visit.seller,
        )
        visit_responses.append(visit_response)

    return visit_responses
