from sqlalchemy import create_engine

from src.core.config import settings


def get_standard_postgres_connection() -> str:
    return (
        f"postgresql://{settings.postgres_user}:{settings.postgres_password}@"
        f"{settings.postgres_host}:{settings.postgres_port}/{settings.postgres_db}"
    )


def get_database_engine() -> create_engine:
    db_url = get_standard_postgres_connection()
    engine = create_engine(db_url, echo=True)

    return engine
