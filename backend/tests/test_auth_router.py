import time

from tests.base_test import BaseTest


class TestAuthRouter(BaseTest):
    create_user_payload = {
        "email": "valid-email@mail.com",
        "full_name": "Test User",
        "nit": "123456789",
        "address": "123 Test St",
        "phone": "1234567890",
        "role": "institutional",
        "password": "my_pass123"
    }

    def test_register_user_with_invalid_parameters(self):
        payload = self.create_user_payload.copy()
        payload["email"] = "invalid-email"
        payload["full_name"] = ""
        payload["nit"] = ""
        payload["address"] = ""
        payload["role"] = "invalid-role"
        payload["password"] = ""

        response = self.client.post(f"{self.prefix}/auth/register", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "email"]
        assert json_response["detail"][1]["loc"] == ["body", "full_name"]
        assert json_response["detail"][2]["loc"] == ["body", "nit"]
        assert json_response["detail"][3]["loc"] == ["body", "address"]
        assert json_response["detail"][4]["loc"] == ["body", "role"]
        assert json_response["detail"][5]["loc"] == ["body", "password"]

    def test_register_user_with_invalid_phone(self):
        payload = self.create_user_payload.copy()
        payload["phone"] = "invalid-phone"

        response = self.client.post(f"{self.prefix}/auth/register", json=payload)
        json_response = response.json()
        assert response.status_code == 400
        assert json_response["message"] == "Phone must be exactly 10 digits"

    def test_create_existing_user(self):
        response1 = self.client.post(f"{self.prefix}/auth/register", json=self.create_user_payload)
        response2 = self.client.post(f"{self.prefix}/auth/register", json=self.create_user_payload)

        assert response1.status_code == 201
        assert response2.status_code == 409
        assert response2.json() == {"message": "User with this email already exists"}

    def test_register_user_successfully(self):
        response = self.client.post(f"{self.prefix}/auth/register", json=self.create_user_payload)
        json_response = response.json()

        assert response.status_code == 201
        assert "id" in json_response
        assert "created_at" in json_response
