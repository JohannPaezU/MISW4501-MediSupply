from src.models.enums.user_role import UserRole
from tests.base_test import BaseTest


class TestSellingPlanRouter(BaseTest):
    create_selling_plan_payload = {
        "period": "3",
        "goal": 100,
    }

    def test_register_selling_plan_with_invalid_parameters(self, authorized_client):
        payload = self.create_selling_plan_payload.copy()
        payload["period"] = ""
        payload["goal"] = -10
        payload["product_id"] = "invalid-uuid"
        payload["zone_id"] = "invalid-uuid"
        payload["seller_id"] = "invalid-uuid"

        response = authorized_client.post(f"{self.prefix}/selling-plans", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "period"]
        assert json_response["detail"][1]["loc"] == ["body", "goal"]
        assert json_response["detail"][2]["loc"] == ["body", "product_id"]
        assert json_response["detail"][3]["loc"] == ["body", "zone_id"]
        assert json_response["detail"][4]["loc"] == ["body", "seller_id"]

    def test_register_selling_plan_success(self, authorized_client):
        payload = self.create_selling_plan_payload.copy()
        payload["product_id"] = next(iter(self.products)).id
        payload["zone_id"] = next(iter(self.zones)).id
        payload["seller_id"] = next(
            user for user in self.users if user.role == UserRole.COMMERCIAL
        ).id

        response = authorized_client.post(f"{self.prefix}/selling-plans", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert json_response["period"] == payload["period"]
        assert json_response["goal"] == payload["goal"]
        assert json_response["product"]["id"] == payload["product_id"]
        assert json_response["zone"]["id"] == payload["zone_id"]
        assert json_response["seller"]["id"] == payload["seller_id"]

    def test_register_selling_plan_duplicate(self, authorized_client):
        payload = self.create_selling_plan_payload.copy()
        payload["product_id"] = next(iter(self.products)).id
        payload["zone_id"] = next(iter(self.zones)).id
        payload["seller_id"] = next(
            user for user in self.users if user.role == UserRole.COMMERCIAL
        ).id

        response1 = authorized_client.post(f"{self.prefix}/selling-plans", json=payload)
        assert response1.status_code == 201

        response2 = authorized_client.post(f"{self.prefix}/selling-plans", json=payload)
        json_response2 = response2.json()
        assert response2.status_code == 409
        assert (
            json_response2["message"]
            == "A selling plan with the same period, product, zone, and seller already exists"
        )

    def test_register_selling_plan_with_nonexistent_product(self, authorized_client):
        payload = self.create_selling_plan_payload.copy()
        payload["product_id"] = "123e4567-e89b-12d3-a456-426614174000"
        payload["zone_id"] = next(iter(self.zones)).id
        payload["seller_id"] = next(
            user for user in self.users if user.role == UserRole.COMMERCIAL
        ).id

        response = authorized_client.post(f"{self.prefix}/selling-plans", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["message"] == "Product with the given ID does not exist"

    def test_register_selling_plan_with_nonexistent_zone(self, authorized_client):
        payload = self.create_selling_plan_payload.copy()
        payload["product_id"] = next(iter(self.products)).id
        payload["zone_id"] = "123e4567-e89b-12d3-a456-426614174000"
        payload["seller_id"] = next(
            user for user in self.users if user.role == UserRole.COMMERCIAL
        ).id

        response = authorized_client.post(f"{self.prefix}/selling-plans", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["message"] == "Zone with the given ID does not exist"

    def test_register_selling_plan_with_nonexistent_seller(self, authorized_client):
        payload = self.create_selling_plan_payload.copy()
        payload["product_id"] = next(iter(self.products)).id
        payload["zone_id"] = next(iter(self.zones)).id
        payload["seller_id"] = "123e4567-e89b-12d3-a456-426614174000"

        response = authorized_client.post(f"{self.prefix}/selling-plans", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["message"] == "Seller with the given ID does not exist"

    def test_get_all_selling_plans(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/selling-plans")
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "selling_plans" in json_response

    def test_get_selling_plan_by_id_not_found(self, authorized_client):
        response = authorized_client.get(
            f"{self.prefix}/selling-plans/123e4567-e89b-12d3-a456-426614174000"
        )
        json_response = response.json()
        assert response.status_code == 404
        assert json_response["message"] == "Selling plan not found"

    def test_get_selling_plan_by_id_success(self, authorized_client):
        payload = self.create_selling_plan_payload.copy()
        payload["product_id"] = next(iter(self.products)).id
        payload["zone_id"] = next(iter(self.zones)).id
        payload["seller_id"] = next(
            user for user in self.users if user.role == UserRole.COMMERCIAL
        ).id

        create_response = authorized_client.post(
            f"{self.prefix}/selling-plans", json=payload
        )
        assert create_response.status_code == 201

        create_json_response = create_response.json()
        selling_plan_id = create_json_response["id"]

        response = authorized_client.get(
            f"{self.prefix}/selling-plans/{selling_plan_id}"
        )
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["id"] == selling_plan_id
