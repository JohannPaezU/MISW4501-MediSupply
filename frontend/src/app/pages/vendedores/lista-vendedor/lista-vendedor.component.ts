import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-lista-vendedor',
  standalone: true,
  imports: [],
  templateUrl: './lista-vendedor.component.html',
  styleUrl: './lista-vendedor.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaVendedorComponent { }
