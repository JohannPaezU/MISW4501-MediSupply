import { Routes } from '@angular/router';
import { ListaProductosComponent } from './lista-productos/lista-productos.component';
import { CrearProductoComponent } from './crear-producto/crear-producto.component';
import { CrearProductoMasivoComponent } from './crear-producto-masivo/crear-producto-masivo.component';

export const PRODUCTOS_ROUTES: Routes = [
  {
    path: '',
    component: ListaProductosComponent
  },
  {
    path: 'crear',
    component: CrearProductoComponent
  },
  {
    path: 'crear-masivo',
    component: CrearProductoMasivoComponent
  },

];
