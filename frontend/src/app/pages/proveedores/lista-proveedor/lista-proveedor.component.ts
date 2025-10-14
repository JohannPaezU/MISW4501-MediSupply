import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-lista-proveedor',
  standalone: true,
  imports: [],
  templateUrl: './lista-proveedor.component.html',
  styleUrl: './lista-proveedor.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaProveedorComponent { }
