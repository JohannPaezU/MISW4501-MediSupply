import pytest
from fastapi.testclient import TestClient


class BaseTest:
    client: TestClient
    prefix: str = "/api/v1"

    @pytest.fixture(autouse=True)
    def _inject_client(self, test_client):
        self.client = test_client
