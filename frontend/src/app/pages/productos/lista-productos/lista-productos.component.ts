import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { Producto } from '../../../interfaces/producto.interface';

@Component({
  selector: 'app-lista-productos',
  standalone: true,
  imports: [CommonModule,
    FormsModule,
    MatIconModule,
  ],
  templateUrl: './lista-productos.component.html',
  styleUrl: './lista-productos.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListaProductosComponent {

  constructor(private router: Router) { }
  displayedColumns: string[] = ['producto', 'lote', 'vencimiento', 'bodega', 'acciones'];

  productos: Producto[] = [
    { producto: 'Acetaminofen', lote: '70216372', vencimiento: '23/11/2028', bodega: 'Colombia' },
    { producto: 'Xray dol', lote: '21928475', vencimiento: '23/11/2028', bodega: 'Colombia' },
    { producto: 'Jeringa', lote: '29192933', vencimiento: '23/11/2028', bodega: 'Colombia' },
    { producto: 'Paracetamol', lote: '94746123', vencimiento: '23/11/2028', bodega: 'Colombia' }
  ];

  pageSize = 10;
  currentPage = 1;
  totalItems = 14;

  exportarCSV(): void {
    console.log('Exportar a CSV');
  }

  crearProducto(): void {
    this.router.navigate(['/productos/crear']);
  }

  verDetalle(producto: Producto): void {
    console.log('Ver detalle:', producto);
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize = Number(target.value);
    this.currentPage = 1;
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (this.currentPage < totalPages) {
      this.currentPage++;
    }
  }

  get startItem(): number {
    return (this.currentPage - 1) * this.pageSize + 1;
  }

  get endItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.totalItems);
  }
}
