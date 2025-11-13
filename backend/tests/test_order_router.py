from datetime import date, timedelta
from unittest.mock import patch

import pytest

from src.models.enums.user_role import UserRole
from tests.base_test import BaseTest


class TestOrderRouter(BaseTest):
    def create_order_by_seller_payload(self) -> dict:
        client = next(
            user for user in self.users if user.role == UserRole.INSTITUTIONAL
        )
        return {
            "comments": "Please deliver between 9 AM and 5 PM",
            "delivery_date": (date.today() + timedelta(days=1)).isoformat(),
            "distribution_center_id": next(iter(self.distribution_centers)).id,
            "client_id": str(client.id),
            "products": [{"product_id": next(iter(self.products)).id, "quantity": 20}],
        }

    def create_order_by_client_payload(self) -> dict:
        return {
            "comments": "Leave at the front desk if not available",
            "delivery_date": (date.today() + timedelta(days=1)).isoformat(),
            "distribution_center_id": next(iter(self.distribution_centers)).id,
            "products": [{"product_id": next(iter(self.products)).id, "quantity": 10}],
        }

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_register_order_with_invalid_parameters(
        self, authorized_client, payload_fn
    ):
        payload = getattr(self, payload_fn)()
        payload["delivery_date"] = "invalid-date"
        payload["distribution_center_id"] = "invalid-uuid"
        payload["products"][0]["product_id"] = "invalid-uuid"
        payload["products"][0]["quantity"] = -5

        response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        json_response = response.json()

        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "delivery_date"]
        assert json_response["detail"][1]["loc"] == ["body", "distribution_center_id"]
        assert json_response["detail"][2]["loc"] == [
            "body",
            "products",
            0,
            "product_id",
        ]
        assert json_response["detail"][3]["loc"] == ["body", "products", 0, "quantity"]

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [("commercial_token", "create_order_by_seller_payload")],
        indirect=["authorized_client"],
    )
    def test_register_order_by_seller_with_missing_client_id(
        self, authorized_client, payload_fn
    ):
        payload = getattr(self, payload_fn)()
        del payload["client_id"]

        response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        json_response = response.json()

        assert response.status_code == 400
        assert (
            json_response["message"]
            == "Client ID must be provided for commercial users"
        )

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_register_order_with_duplicate_products(
        self, authorized_client, payload_fn
    ):
        payload = getattr(self, payload_fn)()
        product_id = next(iter(self.products)).id
        payload["products"] = [
            {"product_id": product_id, "quantity": 5},
            {"product_id": product_id, "quantity": 10},
        ]
        response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        json_response = response.json()

        assert response.status_code == 400
        assert "Duplicated product IDs in order" in json_response["message"]

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_register_order_with_invalid_distribution_center(
        self, authorized_client, payload_fn
    ):
        payload = getattr(self, payload_fn)()
        payload["distribution_center_id"] = "123e4567-e89b-12d3-a456-426614174000"

        response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        json_response = response.json()

        assert response.status_code == 404
        assert json_response["message"] == "Distribution center or client not found"

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_register_order_with_past_delivery_date(
        self, authorized_client, payload_fn
    ):
        payload = getattr(self, payload_fn)()
        payload["delivery_date"] = (date.today() - timedelta(days=1)).isoformat()

        response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        json_response = response.json()

        assert response.status_code == 400
        assert json_response["message"] == "Delivery date cannot be in the past"

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_register_order_with_invalid_product(self, authorized_client, payload_fn):
        product_id = "123e4567-e89b-12d3-a456-426614174000"
        payload = getattr(self, payload_fn)()
        payload["products"][0]["product_id"] = product_id

        response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        json_response = response.json()

        assert response.status_code == 404
        assert json_response["message"] == f"Product '{product_id}' not found"

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_register_order_with_invalid_stock(self, authorized_client, payload_fn):
        product = next(iter(self.products))
        payload = getattr(self, payload_fn)()
        payload["products"][0]["product_id"] = product.id
        payload["products"][0]["quantity"] = product.stock + 1000
        response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        json_response = response.json()
        assert response.status_code == 409
        assert (
            f"Insufficient stock for product '{product.name}'."
            in json_response["message"]
        )

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_register_order_failed(self, authorized_client, payload_fn):
        payload = getattr(self, payload_fn)()
        with patch(
            "sqlalchemy.orm.Session.commit", side_effect=Exception("Unexpected error")
        ) as mock_db_commit:
            response = authorized_client.post(f"{self.prefix}/orders", json=payload)
            json_response = response.json()

            assert response.status_code == 500
            assert (
                json_response["message"] == "An error occurred while creating the order"
            )
            mock_db_commit.assert_called_once()

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_register_order_success(self, authorized_client, payload_fn):
        payload = getattr(self, payload_fn)()
        response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        json_response = response.json()

        assert response.status_code == 201
        assert json_response["comments"] == payload["comments"]
        assert json_response["delivery_date"] == payload["delivery_date"]
        assert json_response["status"] == "received"
        assert (
            json_response["distribution_center"]["id"]
            == payload["distribution_center_id"]
        )
        assert len(json_response["products"]) == len(payload["products"])

    @pytest.mark.parametrize(
        "authorized_client", ["commercial_token", "institutional_token"], indirect=True
    )
    def test_get_all_orders(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/orders")
        json_response = response.json()

        assert response.status_code == 200
        assert "total_count" in json_response
        assert "orders" in json_response

    @pytest.mark.parametrize(
        "authorized_client", ["commercial_token", "institutional_token"], indirect=True
    )
    def test_get_all_orders_with_filters(self, authorized_client):
        params = {"order_status": "received", "delivery_date": date.today().isoformat(), "distribution_center_id": str(next(iter(self.distribution_centers)).id)}
        response = authorized_client.get(f"{self.prefix}/orders", params=params)
        json_response = response.json()

        assert response.status_code == 200
        assert "total_count" in json_response
        assert "orders" in json_response

    @pytest.mark.parametrize(
        "authorized_client, router_id",
        [
            ("commercial_token", "null"),
            ("institutional_token", "123e4567-e89b-12d3-a456-426614174000"),
        ],
        indirect=["authorized_client"],
    )
    def test_get_all_orders_with_router_filter(self, authorized_client, router_id):
        params = {"route_id": router_id}
        response = authorized_client.get(f"{self.prefix}/orders", params=params)
        json_response = response.json()

        assert response.status_code == 200
        assert "total_count" in json_response
        assert "orders" in json_response

    @pytest.mark.parametrize(
        "authorized_client", ["commercial_token", "institutional_token"], indirect=True
    )
    def test_get_order_by_id_not_found(self, authorized_client):
        response = authorized_client.get(
            f"{self.prefix}/orders/123e4567-e89b-12d3-a456-426614174000"
        )
        json_response = response.json()

        assert response.status_code == 404
        assert json_response["message"] == "Order not found"

    @pytest.mark.parametrize(
        "authorized_client,payload_fn",
        [
            ("commercial_token", "create_order_by_seller_payload"),
            ("institutional_token", "create_order_by_client_payload"),
        ],
        indirect=["authorized_client"],
    )
    def test_get_order_by_id_success(self, authorized_client, payload_fn):
        payload = getattr(self, payload_fn)()
        create_response = authorized_client.post(f"{self.prefix}/orders", json=payload)
        assert create_response.status_code == 201
        order_id = create_response.json()["id"]
        response = authorized_client.get(f"{self.prefix}/orders/{order_id}")
        json_response = response.json()

        assert response.status_code == 200
        assert json_response["id"] == order_id
        assert json_response["comments"] == payload["comments"]
        assert json_response["delivery_date"] == payload["delivery_date"]
        assert json_response["status"] == "received"
        assert (
            json_response["distribution_center"]["id"]
            == payload["distribution_center_id"]
        )
        assert len(json_response["products"]) == len(payload["products"])
