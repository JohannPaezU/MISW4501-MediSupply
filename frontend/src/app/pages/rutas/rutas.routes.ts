import { Routes } from '@angular/router';
import { RutasMapaComponent } from './rutas-mapa/rutas-mapa.component';
import { CrearRutaComponent } from './crear-ruta/crear-ruta.component';
import { ListadoRutasComponent } from './listado-rutas/listado-rutas.component';

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
    path: 'rutas-mapa',
    component: RutasMapaComponent
  },
];
