# Sistema de Reportes de Sprint - MediSupply

Este repositorio contiene herramientas para generar reportes automáticos de sprints utilizando datos de JIRA.

## Descripción

El sistema permite generar gráficos y análisis de:
- Burndown charts de sprint
- Gráficos de velocidad del equipo
- Análisis de valor de negocio por historia de usuario
- Reportes semanales de progreso

## Requisitos Previos

Para utilizar este sistema necesitas:

1. **Cursor IDE** con conexión a MCP de JIRA
2. **Acceso a JIRA** con permisos para leer issues
3. **Python 3.8+** con las dependencias instaladas

## Instalación

1. Clona el repositorio
2. Crea un entorno virtual:
   ```bash
   python -m venv venv
   source venv/bin/activate  # En Windows: venv\Scripts\activate
   ```

3. Instala las dependencias:
   ```bash
   pip install -r requirements.txt
   ```

## Generación de Datos de Reporte

### Para crear archivos de datos semanales (`week_#_reports_data.csv`)

**IMPORTANTE**: Debes tener Cursor conectado con el MCP de JIRA para poder ejecutar esta funcionalidad.

Usa el siguiente prompt en Cursor:

```

## Instrucciones para generar reportes de HU desde Jira

### Objetivo
Crear un archivo CSV con el estado de las Historias de Usuario (HU) y sus subtareas desde Jira.

### Pasos a seguir:

1. **Conectar a Jira:**
   - Usar el proyecto "Proyecto final" (clave: SCRUM)
   - Buscar las siguientes HU por sus claves: SCRUM-71, SCRUM-74, SCRUM-72, SCRUM-45, SCRUM-51, SCRUM-52, SCRUM-53, SCRUM-54, SCRUM-55, SCRUM-75, SCRUM-46, SCRUM-47, SCRUM-48, SCRUM-73

2. **Para cada HU:**
   - **Si tiene subtareas:** Incluir TODAS las subtareas como filas separadas
   - **Si NO tiene subtareas:** Incluir la HU principal como su propia subtarea

3. **Estructura del CSV:**
   - **Nombre del archivo:** `week_[NÚMERO]_reports_data.csv` (ej: week_1_reports_data.csv)
   - **Columnas:** HU, sub_task, status, completion_date
   - **Formato de fecha:** YYYY-MM-DD (ej: 2025-10-10)

4. **Reglas de mapeo:**
   - **HU:** Identificador de la historia de usuario (ej: HU01, HU02, etc.)
   - **sub_task:** Clave de Jira + resumen (ej: "SCRUM-119 - Crear backend")
   - **status:** Estado actual (To Do, In Progress, Done)
   - **completion_date:** Solo para tareas con status "Done", usar la fecha de resolución

5. **Ejemplo de referencia:**
   - Usar el archivo `example_reports_data.csv` como guía de formato

### Notas importantes:
- Incluir TODAS las HU solicitadas, tengan o no subtareas
- Para HU sin subtareas, usar la HU principal como subtarea
- Solo incluir fecha de completion_date para tareas marcadas como "Done"
- Mantener consistencia en el formato de nombres de HU

```

### Estructura del archivo CSV generado

El archivo CSV contendrá las siguientes columnas:
- **HU**: ID de la historia de usuario (ej: SCRUM-71)
- **sub_task**: Descripción de la subtarea
- **status**: Estado actual de la subtarea
- **completion_date**: Fecha cuando la subtarea se marcó como "Done"

## Generación de Gráficos

Una vez que tengas los archivos de datos CSV, puedes generar los gráficos:


### 1. Análisis completo en Jupyter
```bash
jupyter lab sprint_reports_analysis.ipynb
```

## Estructura de Archivos

```
graphics/
├── README.md                           # Este archivo
├── requirements.txt                    # Dependencias de Python
├── sprint_reports_analysis.ipynb       # Notebook principal de análisis
├── HU_business_value_points.csv       # Puntos de valor de negocio por HU
├── week_1_reports_data.csv           # Datos de reporte semana 1
├── week_1/                           # Gráficos generados semana 1
│   ├── business value chart.png
│   ├── sprint burn down chart.png
│   └── velocity chart.png
├── week_2/                           # Gráficos semana 2
├── week_3/                           # Gráficos semana 3
└── ...                               # Más semanas
```

## Configuración del MCP de JIRA

Para conectar Cursor con JIRA:

1. Asegúrate de tener acceso a tu instancia de JIRA
2. Configura las credenciales en Cursor
3. Verifica la conexión ejecutando una búsqueda simple

## Troubleshooting

### Error: "No se pueden encontrar las HU"
- Verifica que los IDs de las historias de usuario sean correctos
- Asegúrate de tener permisos de lectura en JIRA
- Confirma que la conexión MCP esté activa

### Error: "Archivo CSV no encontrado"
- Verifica que el archivo `week_#_reports_data.csv` exista
- Asegúrate de que el nombre del archivo coincida con el esperado

## Contribución

Para contribuir al proyecto:
1. Fork el repositorio
2. Crea una rama para tu feature
3. Realiza tus cambios
4. Envía un pull request

## Contacto

Para dudas o problemas, contacta al equipo de desarrollo de MediSupply.
