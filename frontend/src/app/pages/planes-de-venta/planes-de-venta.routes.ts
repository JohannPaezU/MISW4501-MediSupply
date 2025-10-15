import { Routes } from '@angular/router';
import { ListaPlanVentaComponent } from './lista-plan-venta/lista-plan-venta.component';
import { CrearPlanVentaComponent } from './crear-plan-venta/crear-plan-venta.component';

export const PLANES_DE_VENTA_ROUTES: Routes = [
  {
    path: '',
    component: ListaPlanVentaComponent
  },
  {
    path: 'crear',
    component: CrearPlanVentaComponent
  },
];
