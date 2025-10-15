from unittest.mock import patch

from src.core.config import settings
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
    login_payload = {
        "email": "valid-email@mail.com",
        "password": "my_pass123"
    }

    verify_otp_payload = {
        "email": "valid-email@mail.com",
        "otp_code": "123456"
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

    def test_login_with_invalid_parameters(self):
        payload = self.login_payload.copy()
        payload["email"] = "invalid-email"
        payload["password"] = ""

        response = self.client.post(f"{self.prefix}/auth/login", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "email"]
        assert json_response["detail"][1]["loc"] == ["body", "password"]

    def test_login_with_nonexistent_user(self):
        response = self.client.post(f"{self.prefix}/auth/login", json=self.login_payload)

        assert response.status_code == 401
        assert response.json() == {"message": "Invalid email or password"}

    def test_login_with_incorrect_password(self):
        self.client.post(f"{self.prefix}/auth/register", json=self.create_user_payload)
        payload = self.login_payload.copy()
        payload["password"] = "wrong_passw"

        response = self.client.post(f"{self.prefix}/auth/login", json=payload)

        assert response.status_code == 401
        assert response.json() == {"message": "Invalid email or password"}

    @patch("src.services.auth_service.send_email")
    def test_login_successfully(self, mock_send_email):
        self.client.post(f"{self.prefix}/auth/register", json=self.create_user_payload)

        response = self.client.post(f"{self.prefix}/auth/login", json=self.login_payload)
        json_response = response.json()

        assert response.status_code == 200
        assert json_response["message"] == "OTP generated successfully"
        assert "otp_expiration_minutes" in json_response
        assert json_response["otp_expiration_minutes"] == settings.otp_expiration_minutes
        mock_send_email.assert_called_once()

    def test_verify_otp_with_invalid_parameters(self):
        payload = self.verify_otp_payload.copy()
        payload["email"] = "invalid-email"
        payload["otp_code"] = "invalid-otp"

        response = self.client.post(f"{self.prefix}/auth/verify-otp", json=payload)
        json_response = response.json()

        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "email"]
        assert json_response["detail"][1]["loc"] == ["body", "otp_code"]

    def test_verify_otp_with_nonexistent_user(self):
        response = self.client.post(f"{self.prefix}/auth/verify-otp", json=self.verify_otp_payload)

        assert response.status_code == 401
        assert response.json() == {"message": "Invalid or expired OTP"}

    def test_verify_with_incorrect_otp(self):
        self.client.post(f"{self.prefix}/auth/register", json=self.create_user_payload)
        self.client.post(f"{self.prefix}/auth/login", json=self.login_payload)
        payload = self.verify_otp_payload.copy()
        payload["otp_code"] = "000000"

        response = self.client.post(f"{self.prefix}/auth/verify-otp", json=payload)

        assert response.status_code == 401
        assert response.json() == {"message": "Invalid or expired OTP"}

    @patch("src.services.otp_service.random.randint", return_value=123456)
    def test_verify_otp_successfully(self, mock_randint):
        self.client.post(f"{self.prefix}/auth/register", json=self.create_user_payload)
        self.client.post(f"{self.prefix}/auth/login", json=self.login_payload)

        response = self.client.post(f"{self.prefix}/auth/verify-otp", json=self.verify_otp_payload)
        json_response = response.json()

        assert response.status_code == 200
        assert json_response["message"] == "OTP verified successfully"
        assert "access_token" in json_response
        assert json_response["token_type"] == "bearer"
        mock_randint.assert_called_once()
