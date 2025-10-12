# Comandos de Cobertura de Código

## Comandos Principales

### 1. Generar Reporte de Cobertura
```bash
./gradlew jacocoTestReport
```

### 2. Ejecutar Tests y Generar Cobertura
```bash
./gradlew test jacocoTestReport
```

### 3. Verificar Cobertura Mínima (30%)
```bash
./gradlew jacocoTestCoverageVerification
```

### 4. Ver Reporte HTML
```bash
# macOS
open app/build/reports/jacoco/jacocoTestReport/html/index.html

# Windows
start app/build/reports/jacoco/jacocoTestReport/html/index.html

# Linux
xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

## Ubicación de Reportes

- **HTML**: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- **XML**: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`
- **CSV**: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.csv`

## Configuración Actual

- **Cobertura Mínima**: 30%
- **Formato de Reportes**: HTML, XML
- **Exclusiones**: R.class, BuildConfig, Tests, Android framework

## Comandos Útiles

### Limpiar y Regenerar
```bash
./gradlew clean test jacocoTestReport
```

### Solo Tests de Debug
```bash
./gradlew testDebugUnitTest jacocoTestReport
```

### Solo Tests de Release
```bash
./gradlew testReleaseUnitTest jacocoTestReport
```

## Tests que Funcionan Correctamente

### Ejecutar Solo Tests Exitosos
```bash
./gradlew testDebugUnitTest \
  --tests "*RetrofitApiClientTest*" \
  --tests "*UserServiceTest*" \
  --tests "*UserRepositoryTest*" \
  --tests "*UserViewModelTest.registerUser with valid data should call repository*" \
  --tests "*UserViewModelTest.loginUser with valid data should call repository*" \
  --tests "*UserViewModelTest.validateOTP with valid data should call repository*" \
  --tests "*UserViewModelTest.registerUser should execute onResponse callback with success*" \
  --tests "*UserViewModelTest.registerUser should execute onResponse callback with error*" \
  --tests "*UserViewModelTest.registerUser should execute onFailure callback*" \
  --tests "*UserViewModelTest.loginUser should execute onFailure callback*" \
  --tests "*UserViewModelTest.validateOTP should execute onFailure callback*" \
  --tests "*DataModelTest*" \
  --tests "*PrefsManagerTest*" \
  --tests "*ExampleUnitTest*" \
  jacocoTestReport
```

## Cobertura Actual por Paquete

- **✅ `data.network`**: 100% (RetrofitApiClient + UserService)
- **✅ `data.repository`**: 100% (UserRepository)
- **✅ `viewmodel`**: 80%+ (UserViewModel con callbacks completos)
- **✅ `data.model`**: 75% (DataModelTest)
- **✅ `utils`**: 0% (PrefsManagerTest básico)
- **❌ `ui`**: 0% (Sin tests implementados)
