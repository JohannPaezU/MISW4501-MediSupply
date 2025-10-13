
# MediSupply API (backend)

This repository contains the backend API for the MediSupply project. The API is a small FastAPI application that uses PostgreSQL as the data store and is intended to be run via Docker Compose.

## Table of Contents

- [Table of Contents](#table-of-contents)
- [Quick overview](#quick-overview)
- [Requirements](#requirements)
- [Project structure](#project-structure)
- [Environment variables](#environment-variables)
- [Installation (docker-compose only)](#installation-docker-compose-only)
- [Running tests](#running-tests)
- [API Endpoints](#api-endpoints)
- [Health Check](#health-check)
- [User Registration](#user-registration)
  - [Request Field Validation](#request-field-validation)
- [License](#license)

## Quick overview

- Framework: FastAPI
- Language: Python 3.12
- Database: PostgreSQL (run as a container)
- Run method supported: docker-compose (preferred)

## Requirements

- Docker and Docker Compose installed and running on the host machine.
- Ports used by the services are configured through the `.env` file (see below).

Note: tests are integration tests and require Docker to be running; the test harness will automatically provision any containers it needs (you do not need to start Postgres manually).

## Project structure

Repository tree (top-level, representative) with short descriptions:

```
.
├── Dockerfile                 # Image build for the Python app (runs uvicorn)
├── docker-compose.yml        # Compose file for app + postgres (dev)
├── requirements.txt          # Python dependencies
├── README.md                 # This file
├── .env.template             # Template env vars (see below)
├── src                       # Application source code
│   ├── main.py               # FastAPI app entrypoint (routers & startup)
│   ├── core                  # Core utilities
│   │   ├── logging_config.py 
│   │   └── security.py       
│   ├── db                    # Database connection and utilities
│   │   ├── database.py       
│   │   └── database_util.py  
│   ├── errors                # Custom errors & exception handlers
│   │   ├── errors.py
│   │   └── exception_handlers.py
│   ├── models                # ORM models and enums
│   │   └── db_models.py
│   ├── routers               # API route definitions
│   │   ├── auth_router.py
│   │   └── health_check_router.py
│   ├── schemas               # Pydantic request/response schemas
│   │   └── user_schema.py
│   └── services              # Business logic / service layer
│       └── user_service.py
└── tests                    # Integration tests (use Testcontainers fixtures)
    ├── base_test.py
    ├── conftest.py
    └── test_auth_router.py
```

## Environment variables

The project uses these environment variables. Create a `.env` file in the project root based on `.env.template`:

| Variable | Description | Example Value |
|----------|-------------|---------------|
| `APP_PORT` | Port the FastAPI app listens on | `8000` |
| `POSTGRES_HOST` | Hostname/service name for Postgres | `postgres_db` (docker-compose) or `localhost` |
| `POSTGRES_PORT` | Port for Postgres connection | `5432` |
| `POSTGRES_USER` | Postgres username | `postgres` |
| `POSTGRES_PASSWORD` | Postgres password | `postgres` |
| `POSTGRES_DB` | Postgres database name | `medisupply` |

See `.env.template` for a template with all required variables.

## Installation (docker-compose only)

1. Create a `.env` file in the project root based on `.env.template` (see [Environment variables](#environment-variables) section for details):

```bash
cp .env.template .env
# Edit .env with your values
```

2. Build and start the application and the database using Docker Compose:

```powershell
docker compose up --build
```

3. The API will be available at http://localhost:<APP_PORT>/ (default: 8000). The OpenAPI docs are available at http://localhost:<APP_PORT>/docs and the ReDoc at /redoc.

To stop and remove containers, networks and volumes created by compose:

```powershell
docker compose down
```

## Running tests

The test suite contains integration tests and requires Docker to be running on the host. The test harness (Testcontainers / fixtures) will automatically start any containers needed, so you do NOT need to start Postgres or other services manually.

Run the tests with coverage from the repository root (backend):

```powershell
pytest --cov=src --cov-report=term-missing --cov-report=html --cov-fail-under=90 -v
```

Notes:
- Ensure Docker is running before executing the command above.
- The command produces a coverage report in the `htmlcov/` directory and will fail if coverage falls below 90%.

## API Endpoints

**Base path:** `/api/v1`

### Health Check

- **Endpoint:** `GET /api/v1/health`
- **Description:** Returns service health status and metadata
- **Response:**

```json
{
  "status": "healthy",
  "success": true,
  "time_stamp": "2025-10-12T10:30:00.000Z",
  "service": "API"
}
```

### User Registration

- **Endpoint:** `POST /api/v1/auth/register`
- **Description:** Register a new user account
- **Request Body:**

```json
{
  "email": "user@example.com",
  "full_name": "Jane Doe",
  "nit": "123456789",
  "address": "123 Main St",
  "phone": "0999123456",
  "role": "institutional",
  "password": "secret12"
}
```

- **Response:**

```json
{
  "id": "uuid-string",
  "created_at": "2025-10-12T10:30:00.000Z"
}
```

#### Request Field Validation

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `email` | EmailStr | 5-120 chars | Valid email address |
| `full_name` | string | 1-100 chars | User's full name |
| `nit` | string | 1-50 chars | User's NIT |
| `address` | string | 1-255 chars | User's address |
| `phone` | string | exactly 10 digits | User's phone number |
| `role` | enum | `institutional` or `commercial` | User's role type |
| `password` | string | 6-12 chars | User's password |

**Note:** Interactive API documentation is available at `/docs` (Swagger UI) and `/redoc` (ReDoc) when the server is running.

## License

Copyright © MISW4502 - Proyecto Final 2 - 2025.