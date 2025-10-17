
# MediSupply API (backend)

This repository contains the backend API for the MediSupply project. The API is a FastAPI application that uses PostgreSQL as the data store and is intended to be run via Docker Compose. It provides user authentication with OTP verification and email notifications.

## Table of Contents

- [Quick overview](#quick-overview)
- [Prerequisites](#prerequisites)
- [Requirements](#requirements)
- [Project structure](#project-structure)
- [Environment variables](#environment-variables)
- [Installation (docker-compose only)](#installation-docker-compose-only)
- [Running tests](#running-tests)
- [API Endpoints](#api-endpoints)
  - [Health Check](#health-check)
  - [User Registration](#user-registration)
  - [User Login](#user-login)
  - [OTP Verification](#otp-verification)
- [Live Environment](#live-environment)
  - [Quick Test](#quick-test)
  - [Available Endpoints](#available-endpoints)
- [License](#license)

## Quick overview

- Framework: FastAPI
- Language: Python 3.12
- Database: PostgreSQL (run as a container)
- Authentication: OTP-based with JWT tokens
- Email Service: Configurable email service integration
- Run method supported: docker-compose (preferred)

## Prerequisites

Before running this project, make sure you have the following installed:

- **[Python 3.12+](https://www.python.org/downloads/)** - Programming language runtime
- **[Docker](https://www.docker.com/get-started)** - Container platform for running the application and database

## Requirements

- Docker and Docker Compose installed and running on the host machine.
- Email service API key for OTP delivery (supports any email service provider).
- Ports used by the services are configured through the `.env` file (see below).

Note: tests are integration tests and require Docker to be running; the test harness will automatically provision any containers it needs (you do not need to start Postgres manually).

## Project structure

Repository tree (top-level, representative) with short descriptions:

```
.
├── Dockerfile                 # Docker image build configuration for the Python app (runs uvicorn)
├── docker-compose.yml         # Docker Compose orchestration for app + postgres (development)
├── requirements.txt           # Python package dependencies
├── README.md                  # Project documentation (this file)
├── .env.template              # Template for environment variables configuration
├── .env.test                  # Test environment variables
├── .python-version            # Python version specification
├── pytest.ini                 # Pytest configuration file
├── Procfile                   # Process file for deployment (e.g., Heroku)
├── format_code.ps1            # PowerShell script for code formatting (Windows)
├── format_code.sh             # Bash script for code formatting (Linux/Mac)
├── postman/                   # Postman collection for API testing
├── src/                       # Application source code
│   ├── main.py
│   ├── core/                  # Core utilities and configuration
│   │   ├── config.py
│   │   ├── logging_config.py
│   │   ├── security.py
│   │   └── utils.py
│   ├── db/                    # Database connection and utilities
│   │   ├── database.py
│   │   └── database_util.py
│   ├── errors/                # Custom errors and exception handlers
│   │   ├── errors.py
│   │   └── exception_handlers.py
│   ├── models/                # ORM models and enums
│   │   ├── db_models.py
│   │   └── enums/
│   │       └── user_role.py
│   ├── routers/               # API route definitions
│   │   ├── auth_router.py
│   │   └── health_check_router.py
│   ├── schemas/               # Pydantic request/response schemas
│   │   ├── user_schema.py
│   │   └── auth_schema.py
│   ├── services/              # Business logic / service layer
│   │   ├── user_service.py
│   │   ├── auth_service.py
│   │   ├── email_service.py
│   │   ├── otp_service.py
│   │   └── requests/
│   │       └── email_request.py
│   └── templates/             # Email templates
│       └── otp_template.html
└── tests/                     # Integration tests (use Testcontainers fixtures)
    ├── base_test.py
    ├── conftest.py
    ├── test_auth_router.py
    ├── test_health_check_router.py
    └── containers/
        └── postgres_test_container.py
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
| `OTP_EXPIRATION_MINUTES` | OTP code expiration time in minutes | `5` |
| `JWT_SECRET_KEY` | Secret key for JWT token signing | Random secure string (e.g., generated with `openssl rand -hex 32`) |
| `JWT_ALGORITHM` | Algorithm for JWT encoding | `HS256` |
| `ACCESS_TOKEN_EXPIRE_MINUTES` | JWT token expiration time in minutes | `60` |
| `EMAIL_SENDER` | Sender email address for OTP notifications | `noreply@medisupply.com` |
| `EMAIL_API_KEY` | API key for email service provider | Your email service API key |

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
  "phone": "1234567890",
  "role": "institutional",
  "password": "secret12"
}
```

- **Success Response (201):**

```json
{
  "id": "uuid-string",
  "created_at": "2025-10-12T10:30:00.000Z"
}
```

- **Field Validation:**

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `email` | EmailStr | 5-120 chars | Valid email address |
| `full_name` | string | 1-100 chars | User's full name |
| `nit` | string | 1-50 chars | User's NIT (Tax ID) |
| `address` | string | 1-255 chars | User's address |
| `phone` | string | Phone number (9–15 digits) | User's phone number |
| `role` | enum | `institutional` or `commercial` | User's role type |
| `password` | string | 6-12 chars | User's password |

### User Login

- **Endpoint:** `POST /api/v1/auth/login`
- **Description:** Authenticate a user and send OTP to their email
- **Request Body:**

```json
{
  "email": "user@example.com",
  "password": "secret12"
}
```

- **Success Response (200):**

```json
{
  "message": "OTP generated successfully",
  "otp_expiration_minutes": 5
}
```

- **Notes:**
  - If credentials are valid, a 6-digit OTP is generated and sent to the user's email
  - The OTP expires after the configured time (default: 5 minutes)
  - User must verify the OTP to receive an access token

### OTP Verification

- **Endpoint:** `POST /api/v1/auth/verify-otp`
- **Description:** Verify the OTP and receive a JWT access token
- **Request Body:**

```json
{
  "email": "user@example.com",
  "otp_code": "123456"
}
```

- **Success Response (200):**

```json
{
  "message": "OTP verified successfully",
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "bearer"
}
```

- **Notes:**
  - The OTP must be valid and not expired
  - The returned JWT token should be used in the `Authorization` header for protected endpoints
  - Token format: `Bearer <access_token>`

**Note:** Interactive API documentation is available at `/docs` (Swagger UI) and `/redoc` (ReDoc) when the server is running.

## Live Environment

The API is currently deployed and available in a **staging environment** on Heroku:

**Base URL (Staging):** https://medi-supply-staging-9d42f48051e1.herokuapp.com/api/v1

### Quick Test

You can test the API health endpoint to verify the service is running:

```bash
curl https://medi-supply-staging-9d42f48051e1.herokuapp.com/api/v1/health
```

**Example Response:**
```json
{
  "status": "healthy",
  "success": true,
  "time_stamp": "2025-10-15T10:30:00.000Z",
  "service": "API"
}
```

### Available Endpoints

All the endpoints documented in the [API Endpoints](#api-endpoints) section are available on this staging environment.

**Interactive Documentation:**
- Swagger UI: https://medi-supply-staging-9d42f48051e1.herokuapp.com/docs
- ReDoc: https://medi-supply-staging-9d42f48051e1.herokuapp.com/redoc

## License

Copyright © MISW4502 - Proyecto Final 2 - 2025.