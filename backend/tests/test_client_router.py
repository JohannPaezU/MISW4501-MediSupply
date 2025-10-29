import pytest

from tests.base_test import BaseTest


class TestClientRouter(BaseTest):

    @pytest.mark.parametrize("authorized_client", ["commercial_token"], indirect=True)
    def test_get_seller_clients(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/clients")
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "clients" in json_response
