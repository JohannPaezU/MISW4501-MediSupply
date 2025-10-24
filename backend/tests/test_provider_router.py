from tests.base_test import BaseTest


class TestProviderRouter(BaseTest):
    create_provider_payload = {
        "name": "Test Provider",
        "rit": "RIT123456",
        "city": "Test City",
        "country": "Test Country",
        "image_url": "http://example.com/image.png",
        "email": "test@mail.com",
        "phone": "1234567890",
    }

    def test_register_provider_with_invalid_parameters(self, authorized_client):
        payload = self.create_provider_payload.copy()
        payload["name"] = ""
        payload["rit"] = ""
        payload["city"] = ""
        payload["country"] = ""
        payload["image_url"] = "a" * 300
        payload["email"] = "invalid-email"
        payload["phone"] = "123"

        response = authorized_client.post(f"{self.prefix}/providers", json=payload)
        json_response = response.json()
        assert response.status_code == 422
        assert json_response["detail"][0]["loc"] == ["body", "name"]
        assert json_response["detail"][1]["loc"] == ["body", "rit"]
        assert json_response["detail"][2]["loc"] == ["body", "city"]
        assert json_response["detail"][3]["loc"] == ["body", "country"]
        assert json_response["detail"][4]["loc"] == ["body", "image_url"]
        assert json_response["detail"][5]["loc"] == ["body", "email"]
        assert json_response["detail"][6]["loc"] == ["body", "phone"]

    def test_register_provider_with_invalid_phone(self, authorized_client):
        payload = self.create_provider_payload.copy()
        payload["phone"] = "invalid-phone"

        response = authorized_client.post(f"{self.prefix}/providers", json=payload)
        json_response = response.json()
        assert response.status_code == 400
        assert json_response["message"] == "Phone must be between 9 and 15 digits"

    def test_register_provider_with_duplicate_email(self, authorized_client):
        payload = self.create_provider_payload.copy()

        response1 = authorized_client.post(f"{self.prefix}/providers", json=payload)
        assert response1.status_code == 201

        payload["rit"] = "RIT654321"
        response2 = authorized_client.post(f"{self.prefix}/providers", json=payload)
        json_response2 = response2.json()
        assert response2.status_code == 409
        assert (
            json_response2["message"]
            == "Provider with this email or RIT already exists"
        )

    def test_register_provider_with_duplicate_rit(self, authorized_client):
        payload = self.create_provider_payload.copy()
        response1 = authorized_client.post(f"{self.prefix}/providers", json=payload)
        assert response1.status_code == 201

        payload["email"] = "test2@mail.com"
        response2 = authorized_client.post(f"{self.prefix}/providers", json=payload)
        json_response2 = response2.json()

        assert response2.status_code == 409
        assert (
            json_response2["message"]
            == "Provider with this email or RIT already exists"
        )

    def test_register_provider_success(self, authorized_client):
        payload = self.create_provider_payload.copy()

        response = authorized_client.post(f"{self.prefix}/providers", json=payload)
        json_response = response.json()
        assert response.status_code == 201
        assert json_response["name"] == payload["name"]
        assert json_response["rit"] == payload["rit"]
        assert json_response["city"] == payload["city"]
        assert json_response["country"] == payload["country"]
        assert json_response["image_url"] == payload["image_url"]
        assert json_response["email"] == payload["email"]
        assert json_response["phone"] == payload["phone"]

    def test_get_all_providers(self, authorized_client):
        response = authorized_client.get(f"{self.prefix}/providers")
        json_response = response.json()
        assert response.status_code == 200
        assert "total_count" in json_response
        assert "providers" in json_response

    def test_get_provider_by_id_not_found(self, authorized_client):
        response = authorized_client.get(
            f"{self.prefix}/providers/123e4567-e89b-12d3-a456-426614174000"
        )
        json_response = response.json()
        assert response.status_code == 404
        assert json_response["message"] == "Provider not found"

    def test_get_provider_by_id_success(self, authorized_client):
        payload = self.create_provider_payload.copy()

        create_response = authorized_client.post(
            f"{self.prefix}/providers", json=payload
        )
        assert create_response.status_code == 201

        create_json_response = create_response.json()
        provider_id = create_json_response["id"]

        response = authorized_client.get(f"{self.prefix}/providers/{provider_id}")
        json_response = response.json()
        assert response.status_code == 200
        assert json_response["id"] == provider_id
