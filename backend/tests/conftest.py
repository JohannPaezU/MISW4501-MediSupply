from typing import Any, Generator
from unittest.mock import MagicMock

import pytest
from dotenv import find_dotenv, load_dotenv
from fastapi.testclient import TestClient
from google.cloud import storage
from sqlalchemy import create_engine, func
from sqlalchemy.orm import Session, sessionmaker

from src.core.logging_config import logger
from src.core.security import hash_password
from src.db.database import Base
from src.dependencies.gcp_dependency import StorageClientSingleton
from src.models.db_models import (
    DistributionCenter,
    Order,
    OrderProduct,
    Product,
    Provider,
    User,
    Zone,
    Geolocation, Visit,
)
from src.models.enums.user_role import UserRole
from src.models.enums.visit_status import VisitStatus
from tests.containers.postgres_test_container import PostgresTestContainer


def pytest_configure() -> None:
    logger.info("Configuring test environment...")
    env_file = find_dotenv("../.env.test")
    load_dotenv(env_file, override=True)
    logger.info("Test environment configured.")


@pytest.fixture(scope="session")
def postgres_container() -> Generator[PostgresTestContainer, None, None]:
    container = PostgresTestContainer()
    container.start()
    yield container
    container.stop()


@pytest.fixture(scope="session")
def test_client(
    postgres_container: PostgresTestContainer,
) -> Generator[TestClient, None, None]:
    logger.info("Configuring test client...")
    from src.main import app
    app.dependency_overrides[StorageClientSingleton] = _override_storage_client_singleton
    client = TestClient(app)
    logger.info("Test client configured.")
    yield client


@pytest.fixture(autouse=True)
def setup_teardown_db(
    postgres_container: PostgresTestContainer,
) -> Generator[dict[str, Any], None, None]:
    engine = create_engine(postgres_container.get_connection_url())
    logger.info("Setting up database schema...")
    Base.metadata.create_all(bind=engine)
    logger.info("Database schema created.")
    data = _populate_initial_data(engine)
    logger.info("Waiting for database to stabilize...")
    yield data
    Base.metadata.drop_all(bind=engine)
    logger.info("Database schema dropped.")


def _populate_initial_data(engine: create_engine) -> dict[str, Any]:
    logger.info("Populating initial data...")
    data = {}
    session_factory = sessionmaker(autocommit=False, autoflush=False, bind=engine)
    with session_factory() as db:
        data["zones"] = _populate_zones(db=db)
        data["geolocations"] = _populate_geolocations(db=db)
        data["users"] = _populate_users(db=db)
        data["providers"] = _populate_providers(db=db)
        data["products"] = _populate_products(db=db)
        data["distribution_centers"] = _populate_distribution_centers(db=db)
        data["orders"] = _populate_orders(db=db, products=data["products"])
        data["visits"] = _populate_visits(db=db)
    logger.info("Initial data population complete.")

    return data


def _populate_zones(db: Session) -> list[Zone]:
    zones = [
        Zone(
            description="Bogotá",
        ),
        Zone(
            description="Lima",
        ),
    ]

    db.add_all(zones)
    db.commit()
    for zone in zones:
        db.refresh(zone)
        db.expunge(zone)

    return zones


def _populate_geolocations(db: Session) -> list[Geolocation]:
    geolocations = [
        Geolocation(
            address="123 Main St, Bogotá, Colombia",
            latitude=4.7110,
            longitude=-74.0721
        ),
        Geolocation(
            address="456 Side St, Lima, Peru",
            latitude=-12.0464,
            longitude=-77.0428
        ),
    ]
    db.add_all(geolocations)
    db.commit()
    for geolocation in geolocations:
        db.refresh(geolocation)
        db.expunge(geolocation)

    return geolocations


def _populate_users(db: Session) -> list[User]:
    users = [
        User(
            full_name="Admin",
            email="admin@admin.com",
            hashed_password=hash_password("123456"),
            phone="123456789",
            role=UserRole.ADMIN,
            doi="2222222222-2",
        ),
        User(
            id="11111111-1111-1111-1111-111111111111",
            full_name="Commercial",
            email="commercial@commercial.com",
            hashed_password=hash_password("123456"),
            phone="123456789",
            role=UserRole.COMMERCIAL,
            doi="1111111111-1",
            zone_id=_get_random_zone(db=db).id,
        ),
        User(
            full_name="Institutional",
            email="institutional@institutional.com",
            hashed_password=hash_password("123456"),
            phone="123456789",
            role=UserRole.INSTITUTIONAL,
            doi="0000000000-0",
            seller_id="11111111-1111-1111-1111-111111111111",
            geolocation_id=_get_random_geolocation(db=db).id,
        ),
    ]
    db.add_all(users)
    db.flush()
    db.commit()
    for user in users:
        db.refresh(user)
        db.expunge(user)

    return users


def _populate_providers(db: Session) -> list[Provider]:
    providers = [
        Provider(
            name="Provider One",
            rit="1234567890-1",
            city="City One",
            country="Country One",
            image_url=None,
            email="provider1@example.com",
            phone="987654321",
        ),
        Provider(
            name="Provider Two",
            rit="0987654321-2",
            city="City Two",
            country="Country Two",
            image_url=None,
            email="provider2@example.com",
            phone="123456789",
        ),
    ]

    db.add_all(providers)
    db.commit()
    for provider in providers:
        db.refresh(provider)
        db.expunge(provider)

    return providers


def _populate_products(db: Session) -> list[Product]:
    products = [
        Product(
            name="Product One",
            details="Details for product one.",
            store="Store One",
            batch="BATCH001",
            image_url="http://example.com/product1.jpg",
            due_date="2024-12-31",
            stock=100,
            price_per_unit=10.5,
            provider_id=_get_random_provider(db=db).id,
        ),
        Product(
            name="Product Two",
            details="Details for product two.",
            store="Store Two",
            batch="BATCH002",
            image_url="http://example.com/product2.jpg",
            due_date="2025-01-31",
            stock=200,
            price_per_unit=20.0,
            provider_id=_get_random_provider(db=db).id,
        ),
    ]
    db.add_all(products)
    db.commit()
    for product in products:
        db.refresh(product)
        db.expunge(product)

    return products


def _populate_distribution_centers(db: Session) -> list[DistributionCenter]:
    distribution_centers = [
        DistributionCenter(
            name="Distribution Center One",
            address="123 Main St",
            city="City One",
            country="Country One",
        ),
        DistributionCenter(
            name="Distribution Center Two",
            address="456 Side St",
            city="City Two",
            country="Country Two",
        ),
    ]
    db.add_all(distribution_centers)
    db.commit()
    for dc in distribution_centers:
        db.refresh(dc)
        db.expunge(dc)

    return distribution_centers


def _populate_orders(db: Session, products: list[Product]) -> list[Order]:
    orders = [
        Order(
            comments="First order",
            delivery_date="2024-07-01",
            client_id=db.query(User)
            .filter(User.role == UserRole.INSTITUTIONAL)
            .first()
            .id,
            distribution_center_id=db.query(DistributionCenter).first().id,
        ),
        Order(
            comments="Second order",
            delivery_date="2024-08-01",
            client_id=db.query(User)
            .filter(User.role == UserRole.INSTITUTIONAL)
            .first()
            .id,
            distribution_center_id=db.query(DistributionCenter).first().id,
        ),
    ]
    db.add_all(orders)
    db.flush()
    for order in orders:
        product = next(iter(products))
        order_product = OrderProduct(
            quantity=10,
            order_id=order.id,
            product_id=product.id,
        )
        db.add(order_product)
    db.commit()

    for order in orders:
        db.refresh(order)
        db.expunge(order)

    return orders


def _populate_visits(db: Session) -> list[Visit]:
    visits = [
        Visit(
            expected_date="2024-07-15",
            expected_geolocation_id=_get_random_geolocation(db=db).id,
            client_id=_get_random_client(db=db).id,
            seller_id=_get_random_seller(db=db).id,
        ),
        Visit(
            expected_date="2024-08-15",
            visit_date="2024-08-16 12:00:00",
            observations="Visit completed successfully.",
            visual_evidence_path="visits/evidence1.jpg",
            status=VisitStatus.COMPLETED,
            expected_geolocation_id=_get_random_geolocation(db=db).id,
            report_geolocation_id=_get_random_geolocation(db=db).id,
            client_id=_get_random_client(db=db).id,
            seller_id=_get_random_seller(db=db).id,
        ),
    ]

    db.add_all(visits)
    db.commit()
    for visit in visits:
        db.refresh(visit)
        db.expunge(visit)

    return visits


def _get_random_zone(*, db: Session) -> Zone | None:
    return db.query(Zone).order_by(func.random()).first()


def _get_random_provider(*, db: Session) -> Provider | None:
    return db.query(Provider).order_by(func.random()).first()


def _get_random_geolocation(*, db: Session) -> Geolocation | None:
    return db.query(Geolocation).order_by(func.random()).first()


def _get_random_client(*, db: Session) -> User | None:
    return db.query(User).filter(User.role == UserRole.INSTITUTIONAL).order_by(func.random()).first()


def _get_random_seller(*, db: Session) -> User | None:
    return db.query(User).filter(User.role == UserRole.COMMERCIAL).order_by(func.random()).first()


def _override_storage_client_singleton():
    storage_client_mock = MagicMock(spec=storage.Client)

    bucket_mock = MagicMock()
    blob_mock = MagicMock()

    storage_client_mock.bucket.return_value = bucket_mock
    bucket_mock.blob.return_value = blob_mock
    blob_mock.generate_signed_url.return_value = "https://mocked-url.com/file.jpg"

    return storage_client_mock
