from typing import Generator

import pytest
from dotenv import find_dotenv, load_dotenv
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from src.core.logging_config import logger
from src.db.database import Base
from tests.containers.postgres_test_container import PostgresTestContainer


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
def setup_teardown_db(postgres_container: PostgresTestContainer) -> Generator[None, None, None]:
    engine = create_engine(postgres_container.get_connection_url())
    logger.info("Setting up database schema...")
    Base.metadata.create_all(bind=engine)
    yield
    Base.metadata.drop_all(bind=engine)
    logger.info("Database schema dropped.")
