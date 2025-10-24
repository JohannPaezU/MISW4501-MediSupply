from tests.base_test import BaseTest


class TestZoneRouter(BaseTest):
    def test_get_all_zones(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/zones")
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["total_count"] == len(self.zones)
        assert len(json_response["zones"]) == len(self.zones)
        for i in range(len(self.zones)):
            assert json_response["zones"][i]["id"] == self.zones[i].id
            assert json_response["zones"][i]["description"] == self.zones[i].description

    def test_get_zone_not_found(self, authorized_client):
        response = authorized_client.get(
            f"{self.prefix}/zones/123e4567-e89b-12d3-a456-426614174000"
        )
        json_response = response.json()
        assert response.status_code == 404
        assert json_response["message"] == "Zone not found"

    def test_get_zone_success(self, authorized_client):
        zone = self.zones[0]
        response = authorized_client.get(f"{self.prefix}/zones/{zone.id}")
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["id"] == zone.id
        assert json_response["description"] == zone.description
