from typing import Any

from pydantic import BaseModel, model_serializer


class BaseSchema(BaseModel):
    @model_serializer(mode='wrap')
    def _serialize(self, serializer: Any, info) -> Any:
        data = serializer(self)
        if isinstance(data, dict):
            return {k: v for k, v in data.items() if v is not None}
        return data

    model_config = {"str_strip_whitespace": True, "from_attributes": True}