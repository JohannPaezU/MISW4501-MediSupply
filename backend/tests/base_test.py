import pytest
from fastapi.testclient import TestClient

from src.core.security import create_access_token
from src.models.db_models import (
    DistributionCenter,
    Order,
    Product,
    Provider,
    User,
    Visit,
    Zone,
)
from src.models.enums.user_role import UserRole


class BaseTest:
    client: TestClient
    prefix: str = "/api/v1"
    institutional_token: str
    commercial_token: str
    admin_token: str
    users: list[User]
    providers: list[Provider]
    zones: list[Zone]
    products: list[Product]
    distribution_centers: list[DistributionCenter]
    orders: list[Order]
    visits: list[Visit]

    @pytest.fixture(autouse=True)
    def _inject_client(self, test_client):
        self.client = test_client

    @pytest.fixture
    def authorized_client(self, request):
        token_name = getattr(request, "param", None)
        token = getattr(self, token_name) if token_name else self.admin_token
        client = self.client.__class__(self.client.app)
        client.headers.update({"Authorization": f"Bearer {token}"})

        return client

    @pytest.fixture(autouse=True)
    def _create_authorization_tokens(self, setup_teardown_db):
        self._set_up_test_data(setup_teardown_db=setup_teardown_db)
        institutional_user = next(
            user for user in self.users if user.role == UserRole.INSTITUTIONAL
        )
        commercial_user = next(
            user for user in self.users if user.role == UserRole.COMMERCIAL
        )
        admin_user = next(user for user in self.users if user.role == UserRole.ADMIN)
        self.institutional_token = create_access_token(
            data={
                "sub": str(institutional_user.id),
                "role": institutional_user.role.value,
            }
        )
        self.commercial_token = create_access_token(
            data={"sub": str(commercial_user.id), "role": commercial_user.role.value}
        )
        self.admin_token = create_access_token(
            data={"sub": str(admin_user.id), "role": admin_user.role.value}
        )

    def _set_up_test_data(self, setup_teardown_db):
        self.users = setup_teardown_db["users"]
        self.providers = setup_teardown_db["providers"]
        self.zones = setup_teardown_db["zones"]
        self.products = setup_teardown_db["products"]
        self.distribution_centers = setup_teardown_db["distribution_centers"]
        self.orders = setup_teardown_db["orders"]
        self.visits = setup_teardown_db["visits"]
