
# MediSupply API (backend)

Este repositorio contiene la API backend del proyecto MediSupply. La API es una aplicación FastAPI que utiliza PostgreSQL como base de datos y está diseñada para ejecutarse mediante Docker Compose. Proporciona autenticación de usuarios con verificación OTP y notificaciones por correo electrónico.

**🚀 Entorno de Staging:** [https://medi-supply-staging-9d42f48051e1.herokuapp.com/docs](https://medi-supply-staging-9d42f48051e1.herokuapp.com/docs)

## Tabla de Contenidos


- [Descripción General](#descripción-general)
- [Prerequisitos](#prerequisitos)
- [Requisitos](#requisitos)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Variables de Entorno](#variables-de-entorno)
- [Instalación](#instalación)
- [Ejecución de Pruebas](#ejecución-de-pruebas)
- [Endpoints de la API](#endpoints-de-la-api)
  - [Verificación de Salud](#verificación-de-salud)
  - [Autenticación](#autenticación)
  - [Zonas](#zonas)
  - [Vendedores](#vendedores)
  - [Proveedores](#proveedores)
  - [Productos](#productos)
  - [Planes de Venta](#planes-de-venta)
- [Entorno en Vivo](#entorno-en-vivo)
  - [Prueba Rápida](#prueba-rápida)
  - [Documentación Interactiva](#documentación-interactiva)
- [Licencia](#licencia)

## Descripción General

- Framework: FastAPI
- Lenguaje: Python 3.12
- Base de Datos: PostgreSQL (ejecutada como contenedor)
- Autenticación: Basada en OTP con tokens JWT
- Servicio de Email: Integración configurable con servicio de correo electrónico
- Método de ejecución soportado: docker-compose (preferido)

## Prerequisitos

Antes de ejecutar este proyecto, asegúrese de tener instalado lo siguiente:

- **[Python 3.12+](https://www.python.org/downloads/)** - Entorno de ejecución del lenguaje de programación
- **[Docker](https://www.docker.com/get-started)** - Plataforma de contenedores para ejecutar la aplicación y la base de datos

## Requisitos

- Docker y Docker Compose instalados y ejecutándose en la máquina host.
- Clave API del servicio de correo electrónico para la entrega de OTP (soporta cualquier proveedor de servicios de correo electrónico).
- Los puertos utilizados por los servicios se configuran a través del archivo `.env` (ver más abajo).

Nota: Las pruebas son pruebas de integración y requieren que Docker esté en ejecución; el marco de pruebas provisionará automáticamente cualquier contenedor que necesite (no necesita iniciar Postgres manualmente).

## Estructura del Proyecto

Árbol del repositorio (nivel superior, representativo) con descripciones breves:

```
.
├── Dockerfile                 # Configuración de construcción de imagen Docker para la app Python (ejecuta uvicorn)
├── docker-compose.yml         # Orquestación Docker Compose para app + postgres (desarrollo)
├── requirements.txt           # Dependencias de paquetes Python
├── README.md                  # Documentación del proyecto (este archivo)
├── .env.template              # Plantilla para configuración de variables de entorno
├── .env.test                  # Variables de entorno de prueba
├── .python-version            # Especificación de versión Python
├── pytest.ini                 # Archivo de configuración de Pytest
├── Procfile                   # Archivo de proceso para despliegue (ej., Heroku)
├── format_code.ps1            # Script PowerShell para formateo de código (Windows)
├── format_code.sh             # Script Bash para formateo de código (Linux/Mac)
├── postman/                   # Colección Postman para pruebas de API
├── htmlcov/                   # Reportes de cobertura de código HTML
├── src/                       # Código fuente de la aplicación
│   ├── main.py               # Punto de entrada principal de la aplicación
│   ├── core/                 # Utilidades centrales y configuración
│   │   ├── config.py         
│   │   ├── logging_config.py 
│   │   ├── security.py       
│   │   └── utils.py          
│   ├── db/                   # Conexión y utilidades de base de datos
│   │   ├── database.py       
│   │   └── database_util.py  
│   ├── errors/               # Errores personalizados y manejadores de excepciones
│   │   ├── errors.py         
│   │   └── exception_handlers.py
│   ├── models/               # Modelos ORM y enums
│   │   ├── db_models.py      
│   │   └── enums/            # Enumeraciones
│   │       └── user_role.py  
│   ├── routers/              # Definiciones de rutas de API
│   │   ├── auth_router.py    
│   │   ├── health_check_router.py  
│   │   ├── product_router.py       
│   │   ├── provider_router.py      
│   │   ├── seller_router.py        
│   │   ├── selling_plan_router.py  
│   │   └── zone_router.py          
│   ├── schemas/              # Schemas Pydantic de request/response
│   │   ├── auth_schema.py    
│   │   ├── user_schema.py    
│   │   ├── product_schema.py 
│   │   ├── provider_schema.py 
│   │   ├── seller_schema.py   
│   │   ├── selling_plan_schema.py 
│   │   └── zone_schema.py     
│   ├── services/             # Lógica de negocio / capa de servicio
│   │   ├── auth_service.py   
│   │   ├── user_service.py   
│   │   ├── email_service.py  
│   │   ├── otp_service.py    
│   │   ├── product_service.py 
│   │   ├── provider_service.py 
│   │   ├── seller_service.py   
│   │   ├── selling_plan_service.py 
│   │   ├── zone_service.py     
│   │   └── requests/           # Modelos de request
│   │       └── email_request.py 
│   └── templates/            # Plantillas de correo electrónico
│       ├── otp_template.html 
│       └── temporary_password_template.html 
└── tests/                    # Pruebas de integración (usan fixtures de Testcontainers)
    ├── base_test.py          # Clase base para pruebas
    ├── conftest.py           # Configuración de fixtures pytest
    ├── test_auth_router.py   
    ├── test_health_check_router.py 
    ├── test_product_router.py      
    ├── test_provider_router.py     
    ├── test_seller_router.py       
    ├── test_selling_plan_router.py 
    ├── test_zone_router.py         
    ├── test_security_access.py     
    └── containers/                 # Contenedores de prueba
        └── postgres_test_container.py 
```

## Variables de Entorno

El proyecto utiliza estas variables de entorno. Cree un archivo `.env` en la raíz del proyecto basado en `.env.template`:

| Variable | Descripción | Valor de Ejemplo |
|----------|-------------|------------------|
| `APP_PORT` | Puerto en el que escucha la aplicación FastAPI | `8000` |
| `CORS_ORIGINS` | Orígenes permitidos para CORS (separados por coma) | `http://localhost:3000,http://localhost:8080` |
| `LOGIN_URL` | URL de login del frontend | `http://localhost:3000/login` |
| `POSTGRES_HOST` | Hostname/nombre de servicio para Postgres | `postgres_db` (docker-compose) o `localhost` |
| `POSTGRES_PORT` | Puerto para conexión Postgres | `5432` |
| `POSTGRES_USER` | Nombre de usuario Postgres | `postgres` |
| `POSTGRES_PASSWORD` | Contraseña Postgres | `postgres` |
| `POSTGRES_DB` | Nombre de la base de datos Postgres | `medisupply` |
| `OTP_EXPIRATION_MINUTES` | Tiempo de expiración del código OTP en minutos | `5` |
| `JWT_SECRET_KEY` | Clave secreta para firma de tokens JWT | Cadena segura aleatoria (ej., generada con `openssl rand -hex 32`) |
| `JWT_ALGORITHM` | Algoritmo para codificación JWT | `HS256` |
| `ACCESS_TOKEN_EXPIRE_MINUTES` | Tiempo de expiración del token JWT en minutos | `60` |
| `EMAIL_SENDER` | Dirección de correo del remitente para notificaciones OTP | `noreply@medisupply.com` |
| `EMAIL_API_KEY` | Clave API del proveedor de servicio de correo | Su clave API del servicio de correo |

Vea `.env.template` para una plantilla con todas las variables requeridas.

## Instalación

1. Cree un archivo `.env` en la raíz del proyecto basado en `.env.template` (consulte la sección [Variables de Entorno](#variables-de-entorno) para más detalles):

```powershell
cp .env.template .env
# Edite .env con sus valores
```

2. Construya e inicie la aplicación y la base de datos usando Docker Compose:

```powershell
docker compose up --build
```

3. La API estará disponible en http://localhost:<APP_PORT>/ (por defecto: 8000). La documentación OpenAPI está disponible en http://localhost:<APP_PORT>/docs y ReDoc en /redoc.

Para detener y eliminar contenedores, redes y volúmenes creados por compose:

```powershell
docker compose down
```

## Ejecución de Pruebas

La suite de pruebas contiene pruebas de integración y requiere que Docker esté en ejecución en el host. El marco de pruebas (Testcontainers / fixtures) iniciará automáticamente cualquier contenedor necesario, por lo que NO necesita iniciar Postgres u otros servicios manualmente.

Ejecute las pruebas con cobertura desde la raíz del repositorio (backend):

```powershell
pytest --cov=src --cov-report=term-missing --cov-report=html --cov-fail-under=90 -v
```

Notas:
- Asegúrese de que Docker esté en ejecución antes de ejecutar el comando anterior.
- El comando produce un reporte de cobertura en el directorio `htmlcov/` y fallará si la cobertura cae por debajo del 90%.

## Endpoints de la API

**Ruta base:** `/api/v1`

La API proporciona los siguientes endpoints:

### Verificación de Salud
- **GET** `/health` - Retorna el estado de salud del servicio y metadatos

### Autenticación
- **POST** `/auth/register` - Registrar una nueva cuenta de usuario
- **POST** `/auth/login` - Autenticar un usuario y enviar OTP a su correo electrónico
- **POST** `/auth/verify-otp` - Verificar el OTP y recibir un token de acceso JWT
- **GET** `/auth/permissions` - Obtener los endpoints accesibles según el rol del usuario autenticado

### Zonas
- **GET** `/zones` - Obtener la lista de todas las zonas disponibles

### Vendedores
- **GET** `/sellers` - Obtener la lista de todos los vendedores registrados
- **POST** `/sellers` - Registrar un nuevo vendedor
- **GET** `/sellers/{seller_id}` - Obtener los detalles de un vendedor específico por su ID

### Proveedores
- **GET** `/providers` - Obtener la lista de todos los proveedores registrados
- **POST** `/providers` - Registrar un nuevo proveedor en el sistema
- **GET** `/providers/{provider_id}` - Obtener los detalles de un proveedor específico por su ID

### Productos
- **GET** `/products` - Obtener la lista de todos los productos disponibles
- **POST** `/products` - Registrar un nuevo producto en el sistema
- **POST** `/products-batch` - Registrar múltiples productos de forma masiva
- **GET** `/products/{product_id}` - Obtener los detalles de un producto específico por su ID

### Planes de Venta
- **GET** `/selling-plans` - Obtener la lista de todos los planes de venta
- **POST** `/selling-plans` - Crear un nuevo plan de venta
- **GET** `/selling-plans/{selling_plan_id}` - Obtener los detalles de un plan de venta específico por su ID

**Nota:** La documentación interactiva de la API está disponible en `/docs` (Swagger UI) y `/redoc` (ReDoc) cuando el servidor está en ejecución.

## Entorno en Vivo

La API está actualmente desplegada y disponible en un **entorno de staging** en Heroku:

**URL Base (Staging):** https://medi-supply-staging-9d42f48051e1.herokuapp.com/api/v1

### Prueba Rápida

Puede probar el endpoint de salud de la API para verificar que el servicio está funcionando:

```bash
curl https://medi-supply-staging-9d42f48051e1.herokuapp.com/api/v1/health
```

**Respuesta de Ejemplo:**
```json
{
  "status": "healthy",
  "success": true,
  "time_stamp": "2025-10-18T10:30:00.000Z",
  "service": "API"
}
```

### Documentación Interactiva

Todos los endpoints documentados en la sección [Endpoints de la API](#endpoints-de-la-api) están disponibles en este entorno de staging.

**Documentación Interactiva:**
- Swagger UI: https://medi-supply-staging-9d42f48051e1.herokuapp.com/docs
- ReDoc: https://medi-supply-staging-9d42f48051e1.herokuapp.com/redoc

## Licencia

Copyright © MISW4502 - Proyecto Final 2 - 2025.