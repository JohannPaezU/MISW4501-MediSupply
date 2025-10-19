from typing import Generator, Any

import pytest
from dotenv import find_dotenv, load_dotenv
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Session

from src.core.logging_config import logger
from src.db.database import Base
from tests.containers.postgres_test_container import PostgresTestContainer
from src.core.security import hash_password
from src.models.db_models import User, Provider, Zone, Product
from src.models.enums.user_role import UserRole


def pytest_configure() -> None:
    logger.info("Configuring test environment...")
    env_file = find_dotenv('../.env.test')
    load_dotenv(env_file, override=True)
    logger.info("Test environment configured.")


@pytest.fixture(scope="session")
def postgres_container() -> Generator[PostgresTestContainer, None, None]:
    container = PostgresTestContainer()
    container.start()
    yield container
    container.stop()


@pytest.fixture(scope="session")
def test_client(postgres_container: PostgresTestContainer) -> Generator[TestClient, None, None]:
    logger.info("Configuring test client...")
    from src.main import app
    client = TestClient(app)
    logger.info("Test client configured.")
    yield client


@pytest.fixture(autouse=True)
def setup_teardown_db(postgres_container: PostgresTestContainer) -> Generator[dict[str, Any], None, None]:
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
        data["users"] = _populate_users(db=db)
        data["providers"] = _populate_providers(db=db)
        data["zones"] = _populate_zones(db=db)
        data["products"] = _populate_products(db=db, providers=data["providers"])
    logger.info("Initial data population complete.")

    return data


def _populate_users(db: Session) -> list[User]:
    users = [
        User(
            full_name="Institutional",
            email="institutional@institutional.com",
            hashed_password=hash_password("123456"),
            phone="123456789",
            role=UserRole.INSTITUTIONAL,
            doi="0000000000-0",
        ),
        User(
            full_name="Commercial",
            email="commercial@commercial.com",
            hashed_password=hash_password("123456"),
            phone="123456789",
            role=UserRole.COMMERCIAL,
            doi="1111111111-1",
        ),
        User(
            full_name="Admin",
            email="admin@admin.com",
            hashed_password=hash_password("123456"),
            phone="123456789",
            role=UserRole.ADMIN,
            doi="2222222222-2",
        ),
    ]
    db.add_all(users)
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
        )
    ]

    db.add_all(providers)
    db.commit()
    for provider in providers:
        db.refresh(provider)
        db.expunge(provider)

    return providers


def _populate_zones(db: Session) -> list[Provider]:
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


def _populate_products(db: Session, providers: list[Provider]) -> list[Product]:
    products = [
        Product(
            name="Product One",
            details="Details for product one.",
            store="Store One",
            batch="BATCH001",
            image_url="http://example.com/product1.jpg",
            due_date="2024-12-31",
            stock=100,
            price_per_unite=10.5,
            provider_id=next(iter(providers)).id,
        ),
        Product(
            name="Product Two",
            details="Details for product two.",
            store="Store Two",
            batch="BATCH002",
            image_url="http://example.com/product2.jpg",
            due_date="2025-01-31",
            stock=200,
            price_per_unite=20.0,
            provider_id=providers[1].id,
        )
    ]
    db.add_all(products)
    db.commit()
    for product in products:
        db.refresh(product)
        db.expunge(product)

    return products
