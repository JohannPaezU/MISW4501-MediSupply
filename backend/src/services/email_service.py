import smtplib
import ssl
from email.message import EmailMessage

from src.core.config import settings
from src.core.logging_config import logger
from src.errors.errors import ApiError
from src.services.requests.email_request import EmailRequest

context = ssl.create_default_context()


def send_email(email_request: EmailRequest) -> None:  # pragma: no cover
    try:
        email_message = EmailMessage()
        email_message["From"] = settings.email_sender
        email_message["To"] = email_request.email_receiver
        email_message["Subject"] = email_request.email_subject
        email_message.set_content(email_request.email_content, subtype="html")

        with smtplib.SMTP_SSL("smtp.gmail.com", 465, context=context) as smtp:
            smtp.login(settings.email_sender, settings.email_api_key)
            smtp.send_message(email_message)
            logger.info(f"Email sent successfully to [{email_request.email_receiver}]")

    except Exception as e:
        logger.error(f"Failed to send email: {e}")
        raise ApiError(f"Failed to send email: {e}")
