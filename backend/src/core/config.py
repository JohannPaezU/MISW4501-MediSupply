from pydantic import EmailStr
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    postgres_user: str
    postgres_password: str
    postgres_host: str
    postgres_port: int
    postgres_db: str
    otp_expiration_minutes: int = 1
    jwt_secret_key: str
    jwt_algorithm: str
    access_token_expire_minutes: int
    email_sender: EmailStr
    email_api_key: str
    cors_origins: str
    login_url: str
    google_maps_api_key: str

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")


settings = Settings()
