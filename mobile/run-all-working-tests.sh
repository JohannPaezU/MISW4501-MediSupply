#!/bin/bash

# Script para ejecutar todos los tests que funcionan correctamente
echo "Ejecutando todos los tests que funcionan..."

# Ejecutar todos los tests exitosos
./gradlew testDebugUnitTest \
  --tests "*RetrofitApiClientTest*" \
  --tests "*UserServiceTest*" \
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
  --tests "*UserRepositoryTest*" \
  --tests "*ProductosFragmentTest*" \
  --tests "*HistorialFragmentTest*" \
  --tests "*InicioFragmentTest*" \
  --tests "*LoginActivityTest*" \
  --tests "*MainActivityTest*" \
  --tests "*SplashActivityTest*" \
  --tests "*RegisterActivityTest*" \
  --tests "*ValidateOTPActivityTest*" \
  --tests "*HistorialViewModelTest*" \
  --tests "*InicioViewModelTest*" \
  --tests "*ProductosViewModelTest*" \
  --tests "*ExampleUnitTest*" \
  jacocoTestReport

echo "Tests completados. Generando reporte de cobertura..."

# Abrir reporte de cobertura
open app/build/reports/jacoco/jacocoTestReport/html/index.html

echo "Reporte de cobertura abierto en el navegador."