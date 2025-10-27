from unittest.mock import patch

from tests.base_test import BaseTest


class TestProductRouter(BaseTest):
    create_product_payload = {
        "name": "Test Product",
        "details": "This is a test product for unit testing.",
        "store": "Test Store",
        "batch": "BATCH12345",
        "image_url": "http://example.com/image.jpg",
        "due_date": "2025-12-31",
        "stock": 100,
        "price_per_unit": 9.99,
    }

    def test_register_product_with_invalid_parameters(self, authorized_client):
        payload = self.create_product_payload.copy()
        payload["name"] = "ab"
        payload["details"] = "short"
        payload["store"] = ""
        payload["batch"] = "123"
        payload["due_date"] = "invalid-date"
        payload["stock"] = -10
        payload["price_per_unit"] = 0
        payload["provider_id"] = "invalid-uuid"

        response = authorized_client.post(f"{self.prefix}/products", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "name"]
        assert json_response["detail"][1]["loc"] == ["body", "details"]
        assert json_response["detail"][2]["loc"] == ["body", "store"]
        assert json_response["detail"][3]["loc"] == ["body", "batch"]
        assert json_response["detail"][4]["loc"] == ["body", "due_date"]
        assert json_response["detail"][5]["loc"] == ["body", "stock"]
        assert json_response["detail"][6]["loc"] == ["body", "price_per_unit"]
        assert json_response["detail"][7]["loc"] == ["body", "provider_id"]

    def test_register_product_with_invalid_provider(self, authorized_client):
        payload = self.create_product_payload.copy()
        payload["provider_id"] = "123e4567-e89b-12d3-a456-426614174000"

        response = authorized_client.post(f"{self.prefix}/products", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["message"] == "Provider with the given ID does not exist"

    def test_register_product_success(self, authorized_client):
        payload = self.create_product_payload.copy()
        payload["provider_id"] = next(iter(self.providers)).id

        response = authorized_client.post(f"{self.prefix}/products", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert json_response["name"] == payload["name"]
        assert json_response["details"] == payload["details"]
        assert json_response["store"] == payload["store"]
        assert json_response["batch"] == payload["batch"]
        assert json_response["image_url"] == payload["image_url"]
        assert json_response["due_date"] == payload["due_date"]
        assert json_response["stock"] == payload["stock"]
        assert json_response["price_per_unit"] == payload["price_per_unit"]
        assert json_response["provider"]["id"] == payload["provider_id"]

    def test_register_products_bulk_with_invalid_provider(self, authorized_client):
        payload = {
            "products": [
                {
                    "name": "Bulk Product 1",
                    "details": "Details for bulk product 1.",
                    "store": "Bulk Store",
                    "batch": "BULK12345",
                    "image_url": "http://example.com/bulk1.jpg",
                    "due_date": "2025-11-30",
                    "stock": 50,
                    "price_per_unit": 19.99,
                    "provider_id": "123e4567-e89b-12d3-a456-426614174000",
                },
                {
                    "name": "Bulk Product 2",
                    "details": "Details for bulk product 2.",
                    "store": "Bulk Store",
                    "batch": "BULK67890",
                    "image_url": "http://example.com/bulk2.jpg",
                    "due_date": "2025-10-31",
                    "stock": 75,
                    "price_per_unit": 29.99,
                    "provider_id": next(iter(self.providers)).id,
                },
            ]
        }

        response = authorized_client.post(f"{self.prefix}/products-batch", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert json_response["success"] is False
        assert json_response["rows_total"] == 2
        assert json_response["rows_inserted"] == 1
        assert json_response["errors"] == 1
        assert len(json_response["errors_details"]) == 1

    @patch(
        "src.services.product_service.create_product",
        side_effect=Exception("Database error"),
    )
    def test_register_products_bulk_causing_exception(
        self, mock_create_product, authorized_client
    ):
        payload = {
            "products": [
                {
                    "name": "Bulk Product 1",
                    "details": "Details for bulk product 1.",
                    "store": "Bulk Store",
                    "batch": "BULK12345",
                    "image_url": "http://example.com/bulk1.jpg",
                    "due_date": "2025-11-30",
                    "stock": 50,
                    "price_per_unit": 19.99,
                    "provider_id": next(iter(self.providers)).id,
                },
                {
                    "name": "Bulk Product 2",
                    "details": "Details for bulk product 2.",
                    "store": "Bulk Store",
                    "batch": "BULK67890",
                    "image_url": "http://example.com/bulk2.jpg",
                    "due_date": "2025-10-31",
                    "stock": 75,
                    "price_per_unit": 29.99,
                    "provider_id": next(iter(self.providers)).id,
                },
            ]
        }

        response = authorized_client.post(f"{self.prefix}/products-batch", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert json_response["success"] is False
        assert json_response["rows_total"] == 2
        assert json_response["rows_inserted"] == 0
        assert json_response["errors"] == 2
        assert len(json_response["errors_details"]) == 2
        mock_create_product.assert_called()

    def test_register_products_bulk_success(self, authorized_client):
        payload = {"products": []}
        provider_id = next(iter(self.providers)).id
        for i in range(5):
            product = self.create_product_payload.copy()
            product["name"] = f"Test Product {i}"
            product["provider_id"] = provider_id
            payload["products"].append(product)

        response = authorized_client.post(f"{self.prefix}/products-batch", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert json_response["success"] is True
        assert json_response["rows_total"] == 5
        assert json_response["rows_inserted"] == 5
        assert json_response["errors"] == 0
        assert len(json_response["errors_details"]) == 0

    def test_get_all_products(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/products")
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "products" in json_response
        assert isinstance(json_response["products"], list)

    def test_get_product_not_found(self, authorized_client):
        response = authorized_client.get(
            f"{self.prefix}/products/123e4567-e89b-12d3-a456-426614174000"
        )
        json_response = response.json()
        assert response.status_code == 404
        assert json_response["message"] == "Product not found"

    def test_get_product_success(self, authorized_client):
        product = next(iter(self.products))
        response = authorized_client.get(f"{self.prefix}/products/{product.id}")
        json_response = response.json()

        assert response.status_code == 200
        assert json_response["id"] == product.id
        assert json_response["name"] == product.name
        assert json_response["details"] == product.details
        assert json_response["store"] == product.store
        assert json_response["batch"] == product.batch
        assert json_response["image_url"] == product.image_url
        assert json_response["due_date"] == str(product.due_date)
        assert json_response["stock"] == product.stock
        assert json_response["price_per_unit"] == product.price_per_unit
