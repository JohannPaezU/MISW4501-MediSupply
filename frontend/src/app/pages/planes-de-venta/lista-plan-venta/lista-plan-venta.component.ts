import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { PlanVenta } from '../../../interfaces/planVenta.interface';
import { Router } from '@angular/router';

@Component({
  selector: 'app-lista-plan-venta',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule],
  templateUrl: './lista-plan-venta.component.html',
  styleUrl: './lista-plan-venta.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})

export class ListaPlanVentaComponent {

  constructor(private router: Router) {

  }

  planes: PlanVenta[] = [
    {
      id: 1,
      periodo: 'Setiembre 2025',
      producto: 'Medicamentos',
      metaUnidades: 500,
      zona: 'Bogotá',
      vendedor: 'Miguel Padilla'
    },
    {
      id: 2,
      periodo: 'Setiembre 2025',
      producto: 'Medicamentos',
      metaUnidades: 800,
      zona: 'Bogotá',
      vendedor: 'Juan Cervantes'
    },
    {
      id: 3,
      periodo: 'Setiembre 2025',
      producto: 'Medicamentos',
      metaUnidades: 1200,
      zona: 'Bogotá',
      vendedor: 'Johann Páez'
    }
  ];

  pageSize = 10;
  currentPage = 1;
  totalItems = 14;

  exportarCSV(): void {
    console.log('Exportar a CSV');
  }

  crearPlan(): void {
    this.router.navigate(['/planes-de-venta/crear']);
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
