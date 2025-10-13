from tests.base_test import BaseTest


class TestHealthCheckRouter(BaseTest):

    def test_health_check(self):
        response = self.client.get(f"{self.prefix}/health")
        assert response.status_code == 200
        json_response = response.json()
        assert json_response["status"] == "healthy"
        assert json_response["success"] is True
        assert "time_stamp" in json_response
        assert json_response["service"] == "API"
        assert "hostname" in response.headers
        assert "ip_address" in response.headers
