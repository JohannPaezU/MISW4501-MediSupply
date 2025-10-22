from unittest.mock import patch

import pytest

from tests.base_test import BaseTest


class TestSellerRouter(BaseTest):
    create_seller_payload = {
        "full_name": "John Doe",
        "doi": "123456789",
        "email": "seller@mail.com",
        "phone": "1234567890",
    }

    @pytest.fixture
    def authorized_client(self):
        client = self.client.__class__(self.client.app)
        client.headers.update({"Authorization": f"Bearer {self.admin_token}"})
        return client

    def test_register_seller_with_invalid_parameters(self, authorized_client):
        payload = self.create_seller_payload.copy()
        payload["full_name"] = ""
        payload["doi"] = ""
        payload["email"] = "invalid-email"
        payload["phone"] = "abc123"
        payload["zone_id"] = "invalid-uuid"

        response = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "full_name"]
        assert json_response["detail"][1]["loc"] == ["body", "email"]
        assert json_response["detail"][2]["loc"] == ["body", "phone"]
        assert json_response["detail"][3]["loc"] == ["body", "doi"]
        assert json_response["detail"][4]["loc"] == ["body", "zone_id"]

    def test_register_seller_with_invalid_phone(self, authorized_client):
        payload = self.create_seller_payload.copy()
        payload["phone"] = "12345abcde"

        response = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        json_response = response.json()
        assert response.status_code == 400
        assert json_response["message"] == "Phone must be between 9 and 15 digits"

    def test_register_seller_with_invalid_zone(self, authorized_client):
        payload = self.create_seller_payload.copy()
        payload["zone_id"] = "123e4567-e89b-12d3-a456-426614174000"

        response = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["message"] == "Zone with the given ID does not exist"

    @patch("src.services.seller_service.send_email")
    def test_register_seller_success(self, mock_send_email, authorized_client):
        payload = self.create_seller_payload.copy()
        payload["zone_id"] = next(iter(self.zones)).id

        response = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert json_response["full_name"] == payload["full_name"]
        assert json_response["doi"] == payload["doi"]
        assert json_response["email"] == payload["email"]
        assert json_response["phone"] == payload["phone"]
        assert json_response["zone"]["id"] == payload["zone_id"]
        mock_send_email.assert_called_once()

    @patch("src.services.seller_service.send_email")
    def test_register_seller_with_duplicate_email(
        self, mock_send_email, authorized_client
    ):
        payload = self.create_seller_payload.copy()
        payload["zone_id"] = next(iter(self.zones)).id

        response1 = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        assert response1.status_code == 201

        payload["doi"] = "987654321"
        response2 = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        json_response2 = response2.json()
        assert response2.status_code == 409
        assert (
            json_response2["message"] == "Seller with this email or DOI already exists"
        )
        mock_send_email.assert_called_once()

    @patch("src.services.seller_service.send_email")
    def test_register_seller_with_duplicate_doi(
        self, mock_send_email, authorized_client
    ):
        payload = self.create_seller_payload.copy()
        payload["zone_id"] = next(iter(self.zones)).id

        response1 = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        assert response1.status_code == 201

        payload["email"] = "seller2@mail.com"
        response2 = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        json_response2 = response2.json()
        assert response2.status_code == 409
        assert (
            json_response2["message"] == "Seller with this email or DOI already exists"
        )
        mock_send_email.assert_called_once()

    def test_get_all_sellers(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/sellers")
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "sellers" in json_response

    def test_get_seller_by_id_not_found(self, authorized_client):
        response = authorized_client.get(
            f"{self.prefix}/sellers/123e4567-e89b-12d3-a456-426614174000"
        )
        json_response = response.json()
        assert response.status_code == 404
        assert json_response["message"] == "Seller not found"

    @patch("src.services.seller_service.send_email")
    def test_get_seller_by_id_success(self, mock_send_email, authorized_client):
        payload = self.create_seller_payload.copy()
        payload["zone_id"] = next(iter(self.zones)).id

        create_response = authorized_client.post(f"{self.prefix}/sellers", json=payload)
        assert create_response.status_code == 201

        create_json_response = create_response.json()
        seller_id = create_json_response["id"]

        response = authorized_client.get(f"{self.prefix}/sellers/{seller_id}")
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["id"] == seller_id
        mock_send_email.assert_called_once()
