import pytest

from tests.base_test import BaseTest


class TestZoneRouter(BaseTest):

    @pytest.fixture
    def authorized_client(self):
        client = self.client.__class__(self.client.app)
        client.headers.update({
            "Authorization": f"Bearer {self.admin_token}"
        })
        return client

    def test_get_all_zones(self, authorized_client):
        zones = self.zones
        response = authorized_client.get(f"{self.prefix}/zones")
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["total_count"] == len(zones)
        assert len(json_response["zones"]) == len(zones)
        for i in range(len(zones)):
            assert json_response["zones"][i]["id"] == zones[i].id
            assert json_response["zones"][i]["description"] == zones[i].description
