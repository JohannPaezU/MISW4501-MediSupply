from unittest.mock import patch

from src.core.security import create_access_token
from tests.base_test import BaseTest


class TestSecurityAccess(BaseTest):
    endpoint = f"{BaseTest.prefix}/zones"
    invalid_user_token: str = create_access_token(
        data={"sub": "nonexistent", "role": "invalid"}
    )
    invalid_data_token: str = create_access_token(
        data={"no-sub": "nonexistent", "no-role": "invalid"}
    )

    def test_access_denied_without_token(self):
        response = self.client.get(self.endpoint)
        assert response.status_code == 401
        assert response.json()["detail"] == "Not authenticated"

    def test_access_denied_with_invalid_token(self):
        response = self.client.get(
            self.endpoint, headers={"Authorization": "Bearer invalid-token"}
        )
        assert response.status_code == 401
        assert response.json()["message"] == "Token is invalid or has expired."

    def test_access_denied_with_nonexistent_user_token(self):
        response = self.client.get(
            self.endpoint,
            headers={"Authorization": f"Bearer {self.invalid_user_token}"},
        )
        assert response.status_code == 401
        assert response.json()["message"] == "Token is invalid or has expired."

    def test_access_denied_with_invalid_data_token(self):
        response = self.client.get(
            self.endpoint,
            headers={"Authorization": f"Bearer {self.invalid_data_token}"},
        )
        assert response.status_code == 401
        assert response.json()["message"] == "Token is invalid or has expired."

    @patch("src.core.security.jwt.decode", side_effect=Exception("Unexpected error"))
    def test_access_denied_with_token_causing_exception(self, mock_jwt_decode):
        response = self.client.get(
            self.endpoint,
            headers={"Authorization": f"Bearer {self.invalid_user_token}"},
        )
        assert response.status_code == 500
        assert (
            response.json()["message"]
            == "An error occurred while processing the token."
        )
        mock_jwt_decode.assert_called_once()

    def test_access_denied_for_institutional_user(self):
        response = self.client.get(
            self.endpoint,
            headers={"Authorization": f"Bearer {self.institutional_token}"},
        )
        assert response.status_code == 403
        assert (
            response.json()["message"]
            == "Access denied: requires one of the following roles: admin"
        )

    def test_access_denied_for_commercial_user(self):
        response = self.client.get(
            self.endpoint, headers={"Authorization": f"Bearer {self.commercial_token}"}
        )
        assert response.status_code == 403
        assert (
            response.json()["message"]
            == "Access denied: requires one of the following roles: admin"
        )

    def test_access_granted_for_admin_user(self):
        response = self.client.get(
            self.endpoint, headers={"Authorization": f"Bearer {self.admin_token}"}
        )
        assert response.status_code == 200
