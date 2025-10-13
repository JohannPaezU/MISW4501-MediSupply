from sqlalchemy.orm import declarative_base, sessionmaker

from src.db.database_util import get_database_engine

Base = declarative_base()
_engine = None
_session_factory: sessionmaker | None = None


def init_database():
    global _engine, _session_factory
    _engine = get_database_engine()
    _session_factory = sessionmaker(autocommit=False, autoflush=False, bind=_engine)
    Base.metadata.create_all(bind=_engine)


def get_db():
    if _session_factory is None:
        init_database()
    db = _session_factory()
    try:
        yield db
    finally:
        db.close()
