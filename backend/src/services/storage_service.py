from fastapi import UploadFile
from google.cloud import storage
from datetime import timedelta
from src.core.config import settings
from src.errors.errors import ApiError


def upload_to_gcs(storage_client: storage.Client, file: UploadFile, file_path: str) -> None:
    try:
        bucket = storage_client.bucket(settings.bucket_name)
        blob = bucket.blob(file_path)
        file.file.seek(0)
        blob.upload_from_file(file.file, content_type=file.content_type)
    except Exception as e:
        raise ApiError(f"Failed to upload file to GCS: {e}")


def generate_signed_url(storage_client: storage.Client, blob_name: str, expiration_minutes: int = 5) -> str:
    try:
        bucket = storage_client.bucket(settings.bucket_name)
        blob = bucket.blob(blob_name)
        url = blob.generate_signed_url(expiration=timedelta(minutes=expiration_minutes))

        return url
    except Exception as e:
        raise ApiError(f"Failed to generate signed URL: {e}")
