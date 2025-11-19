from tests.base_test import BaseTest


class TestRouteRouter(BaseTest):
    def create_route_payload(self) -> dict:
        order = next(iter(self.orders))

        return {
            "name": "Route 1",
            "vehicle_plate": "ABC-1234",
            "restrictions": "No heavy loads",
            "distribution_center_id": order.distribution_center_id,
            "order_ids": [order.id],
        }

    def test_register_route_with_invalid_parameters(self, authorized_client):
        payload = self.create_route_payload()
        payload["name"] = ""
        payload["vehicle_plate"] = "A"
        payload["restrictions"] = "x"
        payload["distribution_center_id"] = "invalid-uuid"
        payload["order_ids"] = []
        response = authorized_client.post(f"{self.prefix}/routes", json=payload)
        json_response = response.json()

        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "name"]
        assert json_response["detail"][1]["loc"] == ["body", "vehicle_plate"]
        assert json_response["detail"][2]["loc"] == ["body", "restrictions"]
        assert json_response["detail"][3]["loc"] == ["body", "distribution_center_id"]
        assert json_response["detail"][4]["loc"] == ["body", "order_ids"]

    def test_register_route_with_duplicated_order_ids(self, authorized_client):
        payload = self.create_route_payload()
        payload["order_ids"] = [self.orders[0].id, self.orders[0].id]
        response = authorized_client.post(f"{self.prefix}/routes", json=payload)
        json_response = response.json()

        assert response.status_code == 400
        assert "Duplicated order IDs in route" in json_response["message"]

    def test_register_route_with_nonexistent_order_ids(self, authorized_client):
        payload = self.create_route_payload()
        payload["order_ids"] = ["nonexistent-order-id"]
        response = authorized_client.post(f"{self.prefix}/routes", json=payload)
        json_response = response.json()

        assert response.status_code == 404
        assert "Orders not found" in json_response["message"]

    def test_register_route_with_nonexistent_distribution_center(
        self, authorized_client
    ):
        payload = self.create_route_payload()
        payload["distribution_center_id"] = "12345678-1234-1234-1234-123456789012"
        response = authorized_client.post(f"{self.prefix}/routes", json=payload)
        json_response = response.json()

        assert response.status_code == 404
        assert "Distribution center not found" in json_response["message"]

    def test_register_route_with_mismatched_distribution_center(
        self, authorized_client
    ):
        payload = self.create_route_payload()
        other_distribution_center_id = next(
            dc.id
            for dc in self.distribution_centers
            if dc.id != payload["distribution_center_id"]
        )
        payload["distribution_center_id"] = other_distribution_center_id
        response = authorized_client.post(f"{self.prefix}/routes", json=payload)
        json_response = response.json()

        assert response.status_code == 400
        assert (
            "does not belong to the specified distribution center"
            in json_response["message"]
        )

    def test_register_route_successfully(self, authorized_client):
        payload = self.create_route_payload()
        response = authorized_client.post(f"{self.prefix}/routes", json=payload)
        json_response = response.json()

        assert response.status_code == 201
        assert json_response["name"] == payload["name"]
        assert json_response["vehicle_plate"] == payload["vehicle_plate"]
        assert json_response["restrictions"] == payload["restrictions"]
        assert (
            json_response["distribution_center"]["id"]
            == payload["distribution_center_id"]
        )
        assert len(json_response["orders"]) == len(payload["order_ids"])

    def test_get_all_routes(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/routes")
        json_response = response.json()

        assert response.status_code == 200
        assert "total_count" in json_response
        assert "routes" in json_response

    def test_get_route_by_id_not_found(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/routes/nonexistent-route-id")
        json_response = response.json()

        assert response.status_code == 404
        assert "Route not found" in json_response["message"]

    def test_get_route_by_id_successfully(self, authorized_client):
        payload = self.create_route_payload()
        create_response = authorized_client.post(f"{self.prefix}/routes", json=payload)
        created_route = create_response.json()
        assert create_response.status_code == 201

        route_id = created_route["id"]
        response = authorized_client.get(f"{self.prefix}/routes/{route_id}")
        json_response = response.json()

        assert response.status_code == 200
        assert json_response["id"] == route_id
        assert json_response["name"] == payload["name"]
        assert json_response["vehicle_plate"] == payload["vehicle_plate"]
        assert json_response["restrictions"] == payload["restrictions"]
        assert (
            json_response["distribution_center"]["id"]
            == payload["distribution_center_id"]
        )
        assert len(json_response["orders"]) == len(payload["order_ids"])

    def test_get_route_map_not_found(self, authorized_client):
        response = authorized_client.get(
            f"{self.prefix}/routes/nonexistent-route-id/map"
        )
        json_response = response.json()

        assert response.status_code == 404
        assert "Route not found" in json_response["message"]

    def test_get_route_map_successfully(self, authorized_client):
        payload = self.create_route_payload()
        create_response = authorized_client.post(f"{self.prefix}/routes", json=payload)
        created_route = create_response.json()
        assert create_response.status_code == 201

        route_id = created_route["id"]
        response = authorized_client.get(f"{self.prefix}/routes/{route_id}/map")
        json_response = response.json()

        assert response.status_code == 200
        assert json_response["id"] == route_id
        assert json_response["name"] == payload["name"]
        assert json_response["vehicle_plate"] == payload["vehicle_plate"]
        assert json_response["restrictions"] == payload["restrictions"]
        assert "total_count" in json_response
        assert "stops" in json_response
