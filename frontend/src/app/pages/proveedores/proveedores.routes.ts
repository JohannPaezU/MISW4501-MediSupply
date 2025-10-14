import { Routes } from '@angular/router';
import { ListaProveedorComponent } from './lista-proveedor/lista-proveedor.component';
import { CrearProveedorComponent } from './crear-proveedor/crear-proveedor.component';

export const PROVEEDORES_ROUTES: Routes = [
  {
    path: '',
    component: ListaProveedorComponent
  },
  {
    path: 'crear',
    component: CrearProveedorComponent
  },
];
