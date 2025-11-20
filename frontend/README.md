# MediSupply Frontend

Sistema de gestión para distribución de suministros médicos.


----

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Node.js** (v18.x hasta la 20)
- **npm** (v9.x o superior)
- **Angular CLI** (v17.x)

Verifica las versiones instaladas:

```bash
node --version
npm --version
ng version
```

---

## Instalación

### 2. Instalar dependencias

```bash
npm install
```

### 3. Instalar Angular Material 17 (si no está instalado)

```bash
ng add @angular/material@17
```

Opciones recomendadas:
- **Theme**: Custom o Indigo/Pink
- **Typography**: Yes
- **Animations**: Yes

---

## Estructura del Proyecto

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/          # Componentes reutilizables
│   │   │   └── sidebar/         # Barra lateral de navegación
│   │   ├── pages/               # Páginas principales
│   │   │   ├── home/           # Dashboard principal
│   │   │   ├── vendedores/     # Gestión de vendedores
│   │   │   ├── productos/      # Gestión de productos
│   │   │   ├── proveedores/    # Gestión de proveedores
│   │   │   ├── planes-de-venta/# Planes de venta
│   │   │   └── reportes/       # Reportes y estadísticas
│   │   ├── guards/             # Guards de rutas
│   │   ├── interfaces/         # Interfaces TypeScript
│   │   ├── services/           # Servicios HTTP y lógica de negocio
│   │   ├── app.component.ts    # Componente raíz
│   │   ├── app.routes.ts       # Configuración de rutas
│   │   └── app.config.ts       # Configuración de la aplicación
│   ├── assets/                 # Recursos estáticos
│   ├── environments/           # Variables de entorno
│   ├── styles.css              # Estilos globales
│   └── main.ts                 # Punto de entrada
├── angular.json                # Configuración de Angular
├── package.json                # Dependencias del proyecto
└── README.md                   # Este archivo
```

---

## Comandos de Desarrollo

### Servidor de desarrollo

Inicia el servidor de desarrollo en `http://localhost:4200/`

```bash
ng serve
```

### Compilar para producción

Genera los archivos optimizados en la carpeta `dist/`

```bash
ng build
```

Para producción:

```bash
ng build --configuration production
```

### Ejecutar pruebas

```bash
# Pruebas unitarias
ng test


# Cobertura de código
ng test --code-coverage
```

---

## Configuración

### Variables de Entorno

Configura las variables de entorno en `src/environments/`:

**environment.ts** (desarrollo):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:3000/api'
};
```

**environment.prod.ts** (producción):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.medisupply.com'
};
```
---
## License

Copyright © MISW4502 - Proyecto Final 2 - 2025.
