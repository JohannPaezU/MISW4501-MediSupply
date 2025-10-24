from tests.base_test import BaseTest


class TestDistributionCenterRouter(BaseTest):
    def test_get_all_distribution_centers(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/distribution-centers")
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["total_count"] == len(self.distribution_centers)
        assert len(json_response["distribution_centers"]) == len(self.distribution_centers)
        for i in range(len(self.distribution_centers)):
            assert json_response["distribution_centers"][i]["id"] == self.distribution_centers[i].id
            assert json_response["distribution_centers"][i]["name"] == self.distribution_centers[i].name
            assert json_response["distribution_centers"][i]["address"] == self.distribution_centers[i].address
            assert json_response["distribution_centers"][i]["city"] == self.distribution_centers[i].city
            assert json_response["distribution_centers"][i]["country"] == self.distribution_centers[i].country

    def test_get_distribution_center_not_found(self, authorized_client):
        response = authorized_client.get(
            f"{self.prefix}/distribution-centers/123e4567-e89b-12d3-a456-426614174000"
        )
        json_response = response.json()
        assert response.status_code == 404
        assert json_response["message"] == "Distribution center not found"

    def test_get_distribution_center_success(self, authorized_client):
        distribution_center = self.distribution_centers[0]
        response = authorized_client.get(
            f"{self.prefix}/distribution-centers/{distribution_center.id}"
        )
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["id"] == distribution_center.id
        assert json_response["name"] == distribution_center.name
        assert json_response["address"] == distribution_center.address
        assert json_response["city"] == distribution_center.city
        assert json_response["country"] == distribution_center.country
