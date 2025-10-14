import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login/login.component';
import { RecuperarContrasenaComponent } from './pages/login/recuperar-contrasena/recuperar-contrasena.component';
import { HomeComponent } from './pages/home/home/home.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { ListaProductosComponent } from './pages/productos/lista-productos/lista-productos.component';
import { CrearProductoComponent } from './pages/productos/crear-producto/crear-producto.component';
import { CrearProductoMasivoComponent } from './pages/productos/crear-producto-masivo/crear-producto-masivo.component';
import { PLANES_DE_VENTA_ROUTES } from './pages/planes-de-venta/planes-de-venta.routes';

export const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'recuperar-contrasena', component: RecuperarContrasenaComponent },
      { path: '', redirectTo: '/login', pathMatch: 'full' },
    ],
  },
  {
    path: '',
    component: MainLayoutComponent,
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
    ],
  },
  { path: '**', redirectTo: '/home' },
];
