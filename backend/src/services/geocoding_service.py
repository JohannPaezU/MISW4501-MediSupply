import googlemaps
from pydantic import BaseModel

from src.core.config import settings
from src.core.logging_config import logger
from src.errors.errors import ApiError, BadRequestException


class ValidatedAddress(BaseModel):
    formatted_address: str
    latitude: float
    longitude: float
    precision: str
    raw: dict


def get_validated_address(address: str) -> ValidatedAddress:  # pragma: no cover
    """
    Resolves and validates an address using Google Maps.

    Args:
        address (str): The address to validate.

    Returns:
        ValidatedAddress: A validated and geocoded address.

    Raises:
        BadRequestException: If the address is invalid or not precise enough.
        ApiError: If there is an error communicating with Google Maps.
    """

    if not address or len(address.strip()) < 5:
        raise BadRequestException("Address is too short or empty.")

    address = address.strip()
    gmaps_client = _get_gmaps_client()

    try:
        results = gmaps_client.geocode(address)  # type: ignore
    except Exception as e:
        logger.error(f"Google Maps API error: {e}")
        raise ApiError(f"Error communicating with Google Maps: {e}")

    if not results:
        raise BadRequestException(
            "Address not found. Please provide a valid and complete address."
        )

    result = results[0]
    geometry = result.get("geometry", {})
    location_type = geometry.get("location_type", "UNKNOWN")
    location = geometry.get("location")

    if not location or "lat" not in location or "lng" not in location:
        raise ApiError("Google Maps returned incomplete location data.")

    if location_type not in ("ROOFTOP",):
        raise BadRequestException(
            f"Address is not precise enough (type: {location_type}). Please provide a more specific address."
        )

    validated_address = ValidatedAddress(
        formatted_address=result["formatted_address"],
        latitude=location["lat"],
        longitude=location["lng"],
        precision=location_type,
        raw=result,
    )
    logger.info(f"Validated address: {validated_address}")

    return validated_address


def _get_gmaps_client() -> googlemaps.Client:  # pragma: no cover
    return googlemaps.Client(key=settings.google_maps_api_key)
