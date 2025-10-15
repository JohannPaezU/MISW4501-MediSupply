import os
from testcontainers.postgres import PostgresContainer

from src.core.config import settings
from src.core.logging_config import logger


class PostgresTestContainer:
    postgres_container: PostgresContainer = None

    def __init__(self):
        self.postgres_container = PostgresContainer(
            username=settings.postgres_user,
            password=settings.postgres_password,
            dbname=settings.postgres_db,
        ).with_bind_ports(5432, settings.postgres_port)

    def start(self) -> None:
        logger.info("Starting Postgres test container...")
        self.postgres_container.start()
        logger.info("Postgres test container started.")

    def get_connection_url(self) -> str:
        return self.postgres_container.get_connection_url()

    def stop(self) -> None:
        if self.postgres_container:
            logger.info("Stopping Postgres test container...")
            self.postgres_container.stop()
            logger.info("Postgres test container stopped.")
