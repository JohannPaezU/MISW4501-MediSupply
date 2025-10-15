from typing import Any, Dict

import pystache
from pydantic import BaseModel, EmailStr


class EmailRequest(BaseModel):
    email_receiver: EmailStr
    email_subject: str
    email_content: str

    @classmethod
    def from_template(
        cls,
        html_template: str,
        email_receiver: str,
        email_subject: str,
        template_values: Dict[str, Any],
    ) -> "EmailRequest":
        rendered_content = pystache.render(html_template, template_values)
        return cls(
            email_receiver=email_receiver,
            email_subject=email_subject,
            email_content=rendered_content,
        )
