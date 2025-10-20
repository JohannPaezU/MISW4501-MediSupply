import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login/login.component';
import { HomeComponent } from './pages/home/home/home.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { ObtenerAccesoComponent } from './pages/login/obtener-acceso/obtener-acceso.component';
import { authGuard } from './guards/auth.guard';
import { publicGuard } from './guards/public.guard';


export const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    canActivate: [publicGuard], // Protege para que usuarios logueados no vean el login
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'obtener-acceso', component: ObtenerAccesoComponent },
      { path: '', redirectTo: 'login', pathMatch: 'full' },
    ],
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard], // Protege todas las rutas hijas
    children: [
      { path: 'home', component: HomeComponent },
      {
        path: 'planes-de-venta',
        loadChildren: () => import('./pages/planes-de-venta/planes-de-venta.routes').then(m => m.PLANES_DE_VENTA_ROUTES)
      },
      {
        path: 'productos',
        loadChildren: () => import('./pages/productos/productos.routes').then(m => m.PRODUCTOS_ROUTES)
      },
      {
        path: 'proveedores',
        loadChildren: () => import('./pages/proveedores/proveedores.routes').then(m => m.PROVEEDORES_ROUTES)
      },
      {
        path: 'reportes',
        loadChildren: () => import('./pages/reportes/reportes.routes').then(m => m.REPORTES_ROUTES)
      },
      {
        path: 'vendedores',
        loadChildren: () => import('./pages/vendedores/vendedores.routes').then(m => m.VENDEDORES_ROUTES)
      },
      {
        path: 'rutas',
        loadChildren: () => import('./pages/rutas/rutas.routes').then(m => m.RUTAS_ROUTES)
      },
       // Redirección por defecto para las rutas principales
      { path: '', redirectTo: 'home', pathMatch: 'full' },
    ],
  },
  // Redirección para cualquier ruta no encontrada
  { path: '**', redirectTo: 'home' },
];
