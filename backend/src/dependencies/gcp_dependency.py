import json

from google.cloud import storage
from google.oauth2 import service_account

from src.core.config import settings


class StorageClientSingleton:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            info = json.loads(settings.gcp_credentials)
            credentials = service_account.Credentials.from_service_account_info(info=info)
            cls._instance = storage.Client(credentials=credentials)

        return cls._instance
