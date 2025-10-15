from pydantic_settings import BaseSettings, SettingsConfigDict
from pydantic import EmailStr


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

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")


settings = Settings()
