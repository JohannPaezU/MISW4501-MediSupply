import os

from sqlalchemy import create_engine


def get_standard_postgres_connection() -> str:
    db_user = os.environ["POSTGRES_USER"]
    db_password = os.environ["POSTGRES_PASSWORD"]
    db_host = os.environ["POSTGRES_HOST"]
    db_port = os.environ["POSTGRES_PORT"]
    db_name = os.environ["POSTGRES_DB"]

    return f"postgresql://{db_user}:{db_password}@{db_host}:{db_port}/{db_name}"


def get_database_engine() -> create_engine:
    db_url = get_standard_postgres_connection()
    engine = create_engine(db_url, echo=True)

    return engine
