# MediSupply - Aplicación Móvil Android

## 📱 Descripción

MediSupply es una aplicación móvil Android desarrollada en Kotlin que forma parte del sistema de gestión de suministros médicos. La aplicación permite a los usuarios gestionar productos médicos, realizar seguimiento de historial y administrar su cuenta de usuario.

## 🏗️ Arquitectura del Proyecto

### Estructura de Directorios

```text
mobile/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mfpe/medisupply/
│   │   │   │   ├── adapters/           # Adaptadores para RecyclerViews
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/          # Modelos de datos (Order, Product, User, etc.)
│   │   │   │   │   ├── network/        # Servicios de red y cliente Retrofit
│   │   │   │   │   └── repository/     # Repositorios de datos
│   │   │   │   ├── ui/
│   │   │   │   │   ├── comercial/      # Fragmentos para usuarios comerciales
│   │   │   │   │   ├── institucional/  # Fragmentos para usuarios institucionales
│   │   │   │   │   └── *.kt           # Actividades principales (Login, Main, Order, etc.)
│   │   │   │   ├── utils/             # Utilidades, constantes y validaciones
│   │   │   │   └── viewmodel/         # ViewModels de MVVM
│   │   │   ├── res/
│   │   │   │   ├── color/             # Definiciones de colores
│   │   │   │   ├── drawable/          # Iconos y recursos gráficos
│   │   │   │   ├── layout/            # Layouts XML de actividades y fragmentos
│   │   │   │   ├── menu/              # Menús de navegación
│   │   │   │   ├── mipmap-*/          # Iconos de la aplicación
│   │   │   │   ├── navigation/        # Navegación entre fragmentos
│   │   │   │   ├── values/            # Strings, colores, estilos y dimensiones
│   │   │   │   └── xml/               # Reglas de backup y extracción de datos
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                      # Pruebas unitarias
│   │   │   └── java/com/mfpe/medisupply/
│   │   │       ├── data/              # Pruebas de repositorios y servicios
│   │   │       ├── integration/       # Pruebas de integración
│   │   │       ├── ui/                # Pruebas de actividades y fragmentos
│   │   │       ├── utils/             # Pruebas de utilidades
│   │   │       └── viewmodel/         # Pruebas de ViewModels
│   │   └── androidTest/               # Pruebas instrumentadas
│   ├── build.gradle                   # Configuración del módulo app
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml            # Gestión de versiones de dependencias
├── build.gradle                      # Configuración del proyecto raíz
├── settings.gradle                   # Configuración de módulos
├── gradle.properties                 # Propiedades de Gradle
├── gradlew                          # Wrapper de Gradle (Unix)
├── gradlew.bat                      # Wrapper de Gradle (Windows)
├── run-all-working-tests.sh         # Script para ejecutar todas las pruebas
└── coverage-commands.md             # Comandos para generar reportes de cobertura
```

## 🛠️ Tecnologías y Dependencias

### Lenguajes y Frameworks

- **Kotlin** 1.9.24
- **Android Gradle Plugin** 8.7.0
- **AndroidX** (Android Jetpack)

### Dependencias Principales

#### UI y Navegación

- `androidx.core:core-ktx` 1.15.0
- `androidx.appcompat:appcompat` 1.7.0
- `com.google.android.material:material` 1.12.0
- `androidx.constraintlayout:constraintlayout` 2.2.1
- `androidx.navigation:navigation-fragment-ktx` 2.8.4
- `androidx.navigation:navigation-ui-ktx` 2.8.4

#### Arquitectura MVVM

- `androidx.lifecycle:lifecycle-livedata-ktx` 2.8.7
- `androidx.lifecycle:lifecycle-viewmodel-ktx` 2.8.7

#### Red y Datos

- `com.squareup.retrofit2:retrofit` 2.11.0
- `com.squareup.retrofit2:converter-gson` 2.11.0
- `com.github.bumptech.glide:glide` 4.16.0

#### Testing

- `junit:junit` 4.13.2
- `androidx.test.ext:junit` 1.2.1
- `androidx.test.espresso:espresso-core` 3.6.1

## 📋 Configuración del Proyecto

### Requisitos del Sistema

- **Android Studio** Arctic Fox o superior
- **JDK** 8 o superior
- **Android SDK** API 29-35
- **Gradle** 8.0+

### Configuración de Compilación

- **Compile SDK**: 35
- **Target SDK**: 34
- **Min SDK**: 29 (Android 10)
- **Version Code**: 1
- **Version Name**: 1.0

### Características Habilitadas

- **View Binding**: Habilitado para binding de vistas
- **Edge-to-Edge**: Soporte para pantallas completas
- **AndroidX**: Migración completa a AndroidX

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd MISW4501-MediSupply/mobile
```

### 2. Configurar Android Studio

1. Abrir Android Studio
2. Seleccionar "Open an existing Android Studio project"
3. Navegar a la carpeta `mobile/` y abrir el proyecto

### 3. Sincronizar Dependencias

```bash
./gradlew build
```

### 4. Ejecutar la Aplicación

- Conectar dispositivo Android o iniciar emulador
- Ejecutar `./gradlew installDebug` o usar Android Studio


## 🎨 Diseño y Recursos

### Colores

- **Pink**: #E85B81 (Color principal)
- **Gray**: #49454F80 (Texto secundario)
- **Dark Gray**: #DDE2E5 (Fondo)
- **Black/White**: Colores estándar

### Strings Localizados

- Interfaz en español
- Mensajes de usuario
- Etiquetas de formularios
- Títulos de navegación

### Permisos Requeridos

- `ACCESS_FINE_LOCATION`: Ubicación precisa
- `ACCESS_COARSE_LOCATION`: Ubicación aproximada
- `INTERNET`: Conexión a internet
- `ACCESS_NETWORK_STATE`: Estado de red

## 🧪 Testing

### Cobertura de Pruebas Actual

- Ubicación: `app/src/test/java/`
- Framework: JUnit 4

### Pruebas de Integración

- Ubicación: `app/src/androidTest/java/`
- Frameworks: JUnit + Espresso

### Ejecutar Pruebas

```bash
# Pruebas unitarias
./gradlew testDebugUnitTest

# Pruebas de integración
./gradlew connectedAndroidTest

# Generar reporte de cobertura
./gradlew test jacocoTestReport

# Abrir el reporte
open app/build/reports/jacoco/jacocoTestReport/html/index.html

```

## 📦 Construcción y Despliegue

### Build Debug

```bash
./gradlew assembleDebug
```

### Build Release

```bash
./gradlew assembleRelease
```

### Instalación

```bash
# Instalar en dispositivo conectado
./gradlew installDebug

# Instalar versión release
./gradlew installRelease
```

## 🔧 Configuración de Desarrollo

### Gradle Wrapper

- **Gradle Version**: 8.0+
- **JVM Args**: `-Xmx2048m -Dfile.encoding=UTF-8`

### Propiedades del Proyecto

- **AndroidX**: Habilitado
- **Kotlin Code Style**: Official
- **Non-transitive R Class**: Habilitado


## 📝 Notas de Desarrollo

### Patrones Utilizados

- **MVVM**: Model-View-ViewModel
- **Repository Pattern**: Abstracción de datos
- **View Binding**: Binding de vistas
- **Navigation Component**: Navegación entre pantallas

### Mejores Prácticas

- Separación de responsabilidades
- Inyección de dependencias manual
- Manejo de estados con ViewModels
- Navegación declarativa

## 🐛 Solución de Problemas

**Versión**: 1.0  
**Última actualización**: 2025
**Desarrollado por**: Equipo MediSupply
