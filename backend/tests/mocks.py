from src.services.geocoding_service import ValidatedAddress

VALIDATED_ADDRESS_MOCK = ValidatedAddress(
    formatted_address="1600 Amphitheatre Parkway, Mountain View, CA 94043, USA",
    latitude=37.4224764,
    longitude=-122.0842499,
    precision="ROOFTOP",
    raw={},
)
