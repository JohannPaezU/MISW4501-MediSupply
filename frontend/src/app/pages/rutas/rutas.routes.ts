import { Routes } from '@angular/router';
import { CrearRutaComponent } from './crear-ruta/crear-ruta.component';
import { ListadoRutasComponent } from './listado-rutas/listado-rutas.component';
import { RutasMapaComponent } from './rutas-mapa/rutas-mapa.component';

export const RUTAS_ROUTES: Routes = [
  {
    path: '',
    component: ListadoRutasComponent
  },
  {
    path: 'crear-ruta',
    component: CrearRutaComponent
  },
   {
    path: 'rutas-mapa/:id',
    component: RutasMapaComponent
  },
];
