from datetime import date, datetime, timedelta
from unittest.mock import patch

import pytest

from src.models.enums.visit_status import VisitStatus
from tests import mocks
from tests.base_test import BaseTest


class TestVisitRouter(BaseTest):
    create_visit_payload = {
        "expected_date": (date.today() + timedelta(days=1)).isoformat(),
        "address": "123 Main St, Anytown, USA",
    }

    report_visit_data = {
        "visit_date": (datetime.today() + timedelta(days=1)).isoformat(),
        "observations": "Client was very satisfied with the service.",
        "latitude": 40.7128,
        "longitude": -74.0060,
    }

    @pytest.mark.parametrize(
        "authorized_client", ["institutional_token"], indirect=True
    )
    def test_register_visit_with_invalid_parameters(self, authorized_client):
        payload = self.create_visit_payload.copy()
        payload["expected_date"] = "invalid-date"
        payload["address"] = ""

        response = authorized_client.post(f"{self.prefix}/visits", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "expected_date"]
        assert json_response["detail"][1]["loc"] == ["body", "address"]

    @pytest.mark.parametrize(
        "authorized_client", ["institutional_token"], indirect=True
    )
    def test_register_visit_with_past_date(self, authorized_client):
        payload = self.create_visit_payload.copy()
        payload["expected_date"] = "2000-01-01"

        response = authorized_client.post(f"{self.prefix}/visits", json=payload)
        json_response = response.json()
        assert response.status_code == 400
        assert json_response["message"] == "Expected date cannot be in the past"

    @pytest.mark.parametrize(
        "authorized_client", ["institutional_token"], indirect=True
    )
    @patch(
        "src.services.geolocation_service.get_validated_address",
        return_value=mocks.VALIDATED_ADDRESS_MOCK,
    )
    def test_register_visit_success(
        self, mock_get_validated_address, authorized_client
    ):
        payload = self.create_visit_payload.copy()

        response = authorized_client.post(f"{self.prefix}/visits", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert "id" in json_response
        assert json_response["expected_date"] == payload["expected_date"]
        assert json_response["status"] == "pending"
        assert "created_at" in json_response
        assert "expected_geolocation" in json_response
        assert "client" in json_response
        assert "seller" in json_response
        mock_get_validated_address.assert_called_once()

    @pytest.mark.parametrize(
        "authorized_client", ["institutional_token"], indirect=True
    )
    def test_register_visit_success_with_default_address(self, authorized_client):
        payload = self.create_visit_payload.copy()
        del payload["address"]

        response = authorized_client.post(f"{self.prefix}/visits", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert "id" in json_response
        assert json_response["expected_date"] == payload["expected_date"]
        assert json_response["status"] == "pending"
        assert "created_at" in json_response
        assert "expected_geolocation" in json_response
        assert "client" in json_response
        assert "seller" in json_response

    @pytest.mark.parametrize(
        "authorized_client", ["commercial_token", "institutional_token"], indirect=True
    )
    def test_get_all_visits(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/visits")
        json_response = response.json()

        assert response.status_code == 200
        assert "total_count" in json_response
        assert "visits" in json_response

    @pytest.mark.parametrize(
        "authorized_client", ["commercial_token", "institutional_token"], indirect=True
    )
    def test_get_all_visits_with_expected_date_filter(self, authorized_client):
        expected_date = (date.today() + timedelta(days=1)).isoformat()
        response = authorized_client.get(
            f"{self.prefix}/visits?expected_date={expected_date}"
        )
        json_response = response.json()

        assert response.status_code == 200
        assert "total_count" in json_response
        assert "visits" in json_response

    @pytest.mark.parametrize(
        "authorized_client", ["commercial_token", "institutional_token"], indirect=True
    )
    def test_get_all_visits_with_status_filter(self, authorized_client):
        visit_status = "pending"
        response = authorized_client.get(
            f"{self.prefix}/visits?visit_status={visit_status}"
        )
        json_response = response.json()

        assert response.status_code == 200
        assert "total_count" in json_response
        assert "visits" in json_response

    @pytest.mark.parametrize(
        "authorized_client", ["commercial_token", "institutional_token"], indirect=True
    )
    def test_get_visit_by_id_not_found(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/visits/9999")
        json_response = response.json()
        assert response.status_code == 404
        assert json_response["message"] == "Visit not found"

    @pytest.mark.parametrize(
        "authorized_client", ["commercial_token", "institutional_token"], indirect=True
    )
    def test_get_visit_by_id_success(self, authorized_client):
        visit = next(iter(self.visits))
        response = authorized_client.get(f"{self.prefix}/visits/{visit.id}")
        json_response = response.json()

        assert response.status_code == 200
        assert json_response["id"] == visit.id
        assert json_response["expected_date"] == visit.expected_date.isoformat()

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_report_visit_with_invalid_parameters(self, authorized_client):
        visit = next(v for v in self.visits if v.status == VisitStatus.PENDING)
        data = self.report_visit_data.copy()
        data["latitude"] = "invalid-latitude"
        data["longitude"] = "invalid-longitude"

        response = authorized_client.patch(
            f"{self.prefix}/visits/{visit.id}/report", data=data
        )
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "latitude"]
        assert json_response["detail"][1]["loc"] == ["body", "longitude"]

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_report_visit_not_found(self, authorized_client):
        unknown_visit_id = "11111567-e89b-12d3-a456-426614174000"
        data = self.report_visit_data.copy()

        response = authorized_client.patch(
            f"{self.prefix}/visits/{unknown_visit_id}/report", data=data
        )
        json_response = response.json()
        assert response.status_code == 404
        assert json_response["message"] == "Visit not found"

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_report_visit_already_reported(self, authorized_client):
        visit = next(v for v in self.visits if v.status == VisitStatus.COMPLETED)
        data = self.report_visit_data.copy()

        response = authorized_client.patch(
            f"{self.prefix}/visits/{visit.id}/report", data=data
        )
        json_response = response.json()
        assert response.status_code == 400
        assert json_response["message"] == "Visit has already been reported"

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_report_visit_with_early_visit_date(self, authorized_client):
        visit = next(v for v in self.visits if v.status == VisitStatus.PENDING)
        data = self.report_visit_data.copy()
        data["visit_date"] = visit.expected_date.replace(
            day=visit.expected_date.day - 1
        ).isoformat()

        response = authorized_client.patch(
            f"{self.prefix}/visits/{visit.id}/report", data=data
        )
        json_response = response.json()
        assert response.status_code == 400
        assert json_response["message"] == "Visit date cannot be before expected date"

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    @patch(
        "src.services.visit_service._validate_visual_evidence",
        side_effect=Exception("Unexpected error"),
    )
    def test_report_visit_unexpected_error(
        self, mock_validate_visual_evidence, authorized_client
    ):
        visit = next(v for v in self.visits if v.status == VisitStatus.PENDING)
        data = self.report_visit_data.copy()

        response = authorized_client.patch(
            f"{self.prefix}/visits/{visit.id}/report", data=data
        )
        json_response = response.json()
        assert response.status_code == 500
        assert json_response["message"] == "An error occurred while reporting the visit"
        mock_validate_visual_evidence.assert_called_once()

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_report_visit_with_invalid_visual_evidence_format(self, authorized_client):
        visit = next(v for v in self.visits if v.status == VisitStatus.PENDING)
        data = self.report_visit_data.copy()
        files = {"visual_evidence": ("evidence.txt", b"not-an-image", "text/plain")}

        response = authorized_client.patch(
            f"{self.prefix}/visits/{visit.id}/report", data=data, files=files
        )
        json_response = response.json()
        assert response.status_code == 400
        assert "Invalid visual evidence file format" in json_response["message"]

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_report_visit_with_invalid_visual_evidence_size(self, authorized_client):
        visit = next(v for v in self.visits if v.status == VisitStatus.PENDING)
        data = self.report_visit_data.copy()
        files = {
            "visual_evidence": (
                "large_image.jpg",
                b"a" * (31 * 1024 * 1024),  # 31 MB
                "image/jpeg",
            )
        }

        response = authorized_client.patch(
            f"{self.prefix}/visits/{visit.id}/report", data=data, files=files
        )
        json_response = response.json()
        assert response.status_code == 400
        assert (
            json_response["message"]
            == "Visual evidence file size exceeds the maximum limit of 30 MB"
        )

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_report_visit_success(self, authorized_client):
        visit = next(v for v in self.visits if v.status == VisitStatus.PENDING)
        data = self.report_visit_data.copy()

        response = authorized_client.patch(
            f"{self.prefix}/visits/{visit.id}/report", data=data
        )
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["id"] == visit.id
        assert json_response["status"] == "completed"
        assert json_response["observations"] == data["observations"]
        assert "visit_date" in json_response
        assert "report_geolocation" in json_response

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_report_visit_success_with_visual_evidence(self, authorized_client):
        visit = next(v for v in self.visits if v.status == VisitStatus.PENDING)
        data = self.report_visit_data.copy()
        files = {"visual_evidence": ("evidence.jpg", b"fake-image-bytes", "image/jpeg")}

        response = authorized_client.patch(
            f"{self.prefix}/visits/{visit.id}/report", data=data, files=files
        )
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["id"] == visit.id
        assert json_response["status"] == "completed"
        assert json_response["observations"] == data["observations"]
        assert "visit_date" in json_response
        assert "report_geolocation" in json_response
        assert "visual_evidence_url" in json_response
