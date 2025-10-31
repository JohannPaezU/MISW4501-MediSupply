from src.models.enums.user_role import UserRole
from tests.base_test import BaseTest


class TestReportRouter(BaseTest):

    def test_get_orders_report_with_no_filters(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/reports/orders")
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "orders" in json_response

    def test_get_orders_report_with_seller_id_filter(self, authorized_client):
        seller = next(user for user in self.users if user.role == UserRole.COMMERCIAL)
        response = authorized_client.get(
            f"{self.prefix}/reports/orders?seller_id={seller.id}"
        )
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "orders" in json_response

    def test_get_orders_report_with_date_range_filter(self, authorized_client):
        start_date = "2024-01-01"
        end_date = "2024-12-31"
        response = authorized_client.get(
            f"{self.prefix}/reports/orders?start_date={start_date}&end_date={end_date}"
        )
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "orders" in json_response

    def test_get_orders_report_with_status_filter(self, authorized_client):
        order_status = "delivered"
        response = authorized_client.get(
            f"{self.prefix}/reports/orders?order_status={order_status}"
        )
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "orders" in json_response
