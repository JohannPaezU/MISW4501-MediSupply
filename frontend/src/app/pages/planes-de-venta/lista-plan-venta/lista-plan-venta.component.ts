import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { PlanVenta } from '../../../interfaces/planVenta.interface';
import { PlanVentaService } from '../../../services/planes-de-venta/planesDeVenta.service';
import { CsvExportService } from '../../../services/utilities/csv.service';

@Component({
  selector: 'app-lista-plan-venta',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  templateUrl: './lista-plan-venta.component.html',
  styleUrls: ['./lista-plan-venta.component.css']
})
export class ListaPlanVentaComponent implements OnInit {
  planesVenta: PlanVenta[] = [];
  isLoading = false;
  error: string | null = null;
  copiedId: string | null = null;

  pageSize = 10;
  currentPage = 1;

  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private router: Router,
    private planVentaService: PlanVentaService,
    private csvExportService: CsvExportService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarPlanesVenta();
  }

  cargarPlanesVenta(): void {
    this.isLoading = true;
    this.error = null;
    this.cdr.detectChanges();

    this.planVentaService
      .getPlanesVenta()
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response) => {
          this.planesVenta = response.selling_plans;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cargar planes de venta:', err);
          this.error = 'No se pudieron cargar los planes de venta. Intente de nuevo más tarde.';
          this.cdr.detectChanges();
        }
      });
  }

  copyToClipboard(id: string): void {
    navigator.clipboard
      .writeText(id)
      .then(() => {
        this.copiedId = id;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.copiedId = null;
          this.cdr.detectChanges();
        }, 1500);
      })
      .catch((err) => console.error('Error al copiar ID: ', err));
  }

  exportarCSV(): void {
    if (this.isListaVacia()) return this.mostrarToastError();

    const data = this.mapearDatosCSV(this.planesVenta);
    const headers = this.obtenerEncabezadosCSV();

    this.csvExportService.exportarACsv(data, 'planes_venta', headers);
    this.showToast('Planes de venta exportados correctamente.', 'success');
  }

  private isListaVacia(): boolean {
    return this.planesVenta.length === 0;
  }

  private mostrarToastError(): void {
    this.showToast('No hay planes de venta para exportar.', 'error');
  }

  private obtenerEncabezadosCSV(): { [key: string]: string } {
    return {
      id: 'ID',
      period: 'Periodo',
      goal: 'Meta (Unidades)',
      product_name: 'Producto',
      zone_description: 'Zona',
      seller_name: 'Vendedor',
      created_at: 'Fecha de creación'
    };
  }

  private mapearDatosCSV(planes: PlanVenta[]): any[] {
    return planes.map(plan => this.mapearPlan(plan));
  }

  private mapearPlan(plan: PlanVenta): any {
    return {
      id: plan.id ?? '',
      period: plan.period,
      goal: plan.goal,
      product_name: this.extraerNombreProducto(plan),
      zone_description: this.extraerDescripcionZona(plan),
      seller_name: this.extraerNombreVendedor(plan),
      created_at: plan.created_at ?? ''
    };
  }

  private extraerNombreProducto(plan: PlanVenta): string {
    return plan.product?.name ?? '';
  }

  private extraerDescripcionZona(plan: PlanVenta): string {
    return plan.zone?.description ?? '';
  }

  private extraerNombreVendedor(plan: PlanVenta): string {
    return plan.seller?.full_name ?? '';
  }
  crearPlanVenta(): void {
    this.router.navigate(['/planes-venta/crear']);
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

  get planesPaginados(): PlanVenta[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.planesVenta.slice(startIndex, endIndex);
  }

  get startItem(): number {
    if (this.totalItems === 0) return 0;
    return (this.currentPage - 1) * this.pageSize + 1;
  }

  get endItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.totalItems);
  }

  get totalItems(): number {
    return this.planesVenta.length;
  }

  showToast(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.toastMessage = null;
      this.cdr.detectChanges();
    }, 4000);
  }
}
