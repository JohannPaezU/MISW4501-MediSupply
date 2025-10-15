import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-listado-rutas',
  standalone: true,
  imports: [],
  templateUrl: './listado-rutas.component.html',
  styleUrl: './listado-rutas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListadoRutasComponent { }
