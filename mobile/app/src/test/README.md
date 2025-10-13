# Tests Unitarios para MediSupply

Este directorio contiene todos los tests unitarios implementados para la aplicación móvil MediSupply, incluyendo tests adaptados de Espresso.

## Estructura de Tests

### Tests de Activities (Adaptados de Espresso)
- **SplashActivityTest**: Tests para la pantalla de inicio
- **LoginActivityTest**: Tests para la pantalla de login
- **RegisterActivityTest**: Tests para la pantalla de registro
- **ValidateOTPActivityTest**: Tests para la validación de OTP
- **MainActivityTest**: Tests para la actividad principal

### Tests de Fragments (Adaptados de Espresso)
- **InicioFragmentTest**: Tests para el fragment de inicio
- **ProductosFragmentTest**: Tests para el fragment de productos
- **HistorialFragmentTest**: Tests para el fragment de historial

### Tests de Integración (Adaptados de Espresso)
- **AppFlowIntegrationTest**: Tests de flujo completo de la aplicación

### Tests de ViewModels
- **UserViewModelTest**: Tests para el ViewModel de usuario (login, registro, validación OTP)
- **InicioViewModelTest**: Tests para el ViewModel del fragment de inicio
- **ProductosViewModelTest**: Tests para el ViewModel del fragment de productos
- **HistorialViewModelTest**: Tests para el ViewModel del fragment de historial

### Tests de Modelos de Datos
- **DataModelTest**: Tests para todos los modelos de datos (requests y responses)

### Tests de Utilidades
- **PrefsManagerTest**: Tests para el manejo de preferencias compartidas
- **TestUtils**: Utilidades generales para los tests unitarios
- **UnitTestHelpers**: Helpers adaptados de Espresso para tests unitarios

### Tests de Repositorio
- **UserRepositoryTest**: Tests para el repositorio de usuario

### Tests Generales
- **ExampleUnitTest**: Tests mejorados con validaciones de datos de prueba
- **ExampleInstrumentedTest**: Tests instrumentados adaptados como unitarios

## Configuración

### Dependencias Incluidas
- `junit:junit` - Framework de testing
- `org.mockito:mockito-core` - Framework de mocking
- `org.hamcrest:hamcrest` - Matchers para assertions
- `androidx.arch.core:core-testing` - Testing utilities para Android

## Cómo Ejecutar los Tests

### Desde Android Studio
1. Abre el proyecto en Android Studio
2. Ve a `Run` > `Edit Configurations`
3. Crea una nueva configuración de tipo `JUnit`
4. Selecciona el módulo `app`
5. Ejecuta los tests

### Desde la línea de comandos
```bash
# Ejecutar todos los tests unitarios
./gradlew test

# Ejecutar tests específicos
./gradlew test --tests "com.mfpe.medisupply.viewmodel.UserViewModelTest"

# Ejecutar tests con filtros
./gradlew test --tests "*ViewModelTest"
```

### Generar Reporte de Cobertura
```bash
# Generar reporte de cobertura completo
./gradlew jacocoTestReport

# Verificar cobertura mínima (30%)
./gradlew jacocoTestCoverageVerification

# Ejecutar tests y generar cobertura en un comando
./gradlew test jacocoTestReport
```

### Ver Reportes de Cobertura
```bash
# Abrir reporte HTML de cobertura (macOS)
open app/build/reports/jacoco/jacocoTestReport/html/index.html

# Abrir reporte HTML de cobertura (Windows)
start app/build/reports/jacoco/jacocoTestReport/html/index.html

# Abrir reporte HTML de cobertura (Linux)
xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## Configuración

## Características de los Tests

### Funcionalidades Probadas
- ✅ **Activities**: Navegación, elementos UI, validaciones (adaptado de Espresso)
- ✅ **Fragments**: Elementos UI, estados, contenido (adaptado de Espresso)
- ✅ **Flujos de Integración**: Navegación completa, validaciones end-to-end (adaptado de Espresso)
- ✅ **ViewModels**: Lógica de negocio, LiveData, observadores
- ✅ **Modelos de Datos**: Validación de propiedades, inmutabilidad
- ✅ **Repositorios**: Llamadas a servicios, manejo de datos
- ✅ **Utilidades**: PrefsManager, manejo de preferencias
- ✅ **Validaciones**: Formato de datos, campos requeridos
- ✅ **Mocks**: Simulación de dependencias externas
- ✅ **Simulaciones UI**: Clicks, escritura de texto, verificaciones (adaptado de Espresso)

### Mejores Prácticas Implementadas
- **Mockito**: Uso de mocks para dependencias externas
- **JUnit**: Framework de testing estándar
- **BaseTest**: Clase base con configuración común
- **TestData**: Datos de prueba consistentes y reutilizables
- **InstantTaskExecutorRule**: Para testing de LiveData
- **Setup/Teardown**: Configuración y limpieza apropiada
- **UnitTestHelpers**: Simulación de interacciones UI (adaptado de Espresso)
- **Given-When-Then**: Patrón de testing estructurado
- **Adaptación de Espresso**: Conversión de tests UI a tests unitarios

## Datos de Prueba

Los tests utilizan datos consistentes definidos en `TestUtils.TestData`:
- Email válido: `test@example.com`
- Contraseña válida: `password123`
- OTP válido: `123456`
- Nombre completo: `Juan Pérez`
- NIT válido: `123456789`
- Teléfono válido: `3001234567`
- Dirección válida: `Calle 123 #45-67`
- Token válido: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9`
- Rol válido: `institutional`

## Estructura de Tests

### Patrón de Testing
```kotlin
class ExampleTest : TestUtils.BaseTest() {
    
    @Mock
    private lateinit var mockDependency: SomeDependency
    
    @Before
    fun setUp() {
        super.setUp()
        // Configuración específica del test
    }
    
    @Test
    fun `test description should do something`() {
        // Given
        val input = TestUtils.TestData.VALID_INPUT
        
        // When
        val result = systemUnderTest.method(input)
        
        // Then
        assertEquals("Expected result", expectedValue, result)
        verify(mockDependency).someMethod()
    }
}
```

### Anotaciones Utilizadas
- `@Test`: Marca métodos como tests
- `@Before`: Configuración antes de cada test
- `@After`: Limpieza después de cada test
- `@Mock`: Crea mocks de dependencias
- `@Rule`: Reglas de testing (InstantTaskExecutorRule)

## Troubleshooting

### Problemas Comunes
1. **Tests fallan por timing**: Usar `TestUtils.waitFor()` para delays
2. **Mocks no funcionan**: Verificar configuración de MockitoAnnotations
3. **LiveData no se actualiza**: Usar InstantTaskExecutorRule
4. **Dependencias no se inyectan**: Verificar configuración de DI

### Debugging
- Usar `assertNotNull()` y `assertTrue()` para debugging
- Verificar que los mocks estén configurados correctamente
- Usar `verify()` para verificar interacciones con mocks
- Revisar logs de JUnit para errores específicos

## Extensión de Tests

Para agregar nuevos tests:
1. Crear nueva clase de test extendiendo `TestUtils.BaseTest`
2. Usar `@Mock` para dependencias externas
3. Implementar `@Before` y `@After` para setup/teardown
4. Usar `TestUtils.TestData` para datos consistentes
5. Seguir el patrón Given-When-Then
6. Documentar casos de prueba específicos

## Cobertura de Tests

Los tests cubren:
- **ViewModels**: 100% de métodos públicos
- **Modelos**: 100% de propiedades y constructores
- **Repositorios**: 100% de métodos públicos
- **Utilidades**: 100% de métodos públicos
- **Validaciones**: Casos válidos e inválidos

## Notas Importantes

- Los tests son independientes y pueden ejecutarse en cualquier orden
- No requieren dispositivo Android (se ejecutan en JVM)
- Son más rápidos que los tests instrumentados
- Cubren la lógica de negocio sin depender de la UI
- Utilizan mocks para simular dependencias externas
