import { Routes } from '@angular/router';
import { ListaVendedorComponent } from './lista-vendedor/lista-vendedor.component';
import { CrearVendedorComponent } from './crear-vendedor/crear-vendedor.component';


export const VENDEDORES_ROUTES: Routes = [
  {
    path: '',
    component: ListaVendedorComponent
  },
  {
    path: 'crear',
    component: CrearVendedorComponent
  },

];
