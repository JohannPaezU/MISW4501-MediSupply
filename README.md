# MISW4501 - MediSupply

Este repositorio contiene el código completo del proyecto **MediSupply**, desarrollado como parte del curso **MISW4501 - Proyecto 2**.  
El sistema está dividido en tres componentes principales, cada uno con responsabilidades específicas:

---

## Estructura del proyecto

| Carpeta | Descripción |
|----------|--------------|
| [`frontend/`](./frontend/README.md) | Aplicación **web** construida con Angular 17. Incluye la interfaz principal del sistema y el módulo de autenticación. |
| [`backend/`](./backend/README.md) | API REST construida con **Python (FastAPI)**. Gestiona la lógica de negocio y la conexión con la base de datos. |
| [`mobile/`](./mobile/README.md) | Aplicación **móvil** desarrollada con **Kotlin**. Ofrece acceso optimizado para dispositivos móviles. |

---

## Características principales

- Arquitectura modular: frontend, backend y mobile independientes.
- CI/CD configurado con **GitHub Actions** para integrar y verificar automáticamente cambios.
- Uso de **entornos de staging y producción** (pendientes de despliegue).
- Pruebas automatizadas en frontend y backend.

---

## Integrantes del equipo

| Nombre                            | Correo                       |
|-----------------------------------|------------------------------|
| Miguel Fernando Padilla Espino    | m.padillae@uniandes.edu.co   |
| Johann Sebastian Páez Campos      | js.paezc1@uniandes.edu.co    |
| Juan Sebastian Cervantes Restrepo | js.cervantes@uniandes.edu.co |
| Julian Esteban Oliveros Forero    | je.oliverosf@uniandes.edu.co |

---

### 1. Clonar el repositorio
```bash
git clone https://github.com/[usuario]/MISW4501-MediSupply.git
cd MISW4501-MediSupply