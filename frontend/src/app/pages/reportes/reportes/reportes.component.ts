import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { forkJoin, finalize } from 'rxjs';
import { PlanVenta } from '../../../interfaces/planVenta.interface';
import { ProductCreateResponse } from '../../../interfaces/producto.interface';
import { ReporteData, FiltrosReporte, EstadisticasReporte } from '../../../interfaces/reporte.interface';
import { PlanVentaService } from '../../../services/planes-de-venta/planesDeVenta.service';
import { ProductService } from '../../../services/productos/product.service';
import { VendedorService } from '../../../services/vendedores/vendedor.service';
import { Vendedor, Zone } from '../../../interfaces/vendedor.interface';
import { ReporteService } from '../../../services/reportes/reportes.service';
import { PdfExportService } from '../../../services/utilities/pdf.service';
import { ExcelExportService } from '../../../services/utilities/excel.service';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule, MatIconModule],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportesComponent implements OnInit {
  reportes: ReporteData[] = [];
  reportesFiltrados: ReporteData[] = [];

  vendedores: Vendedor[] = [];
  zonas: Zone[] = [];
  productos: ProductCreateResponse[] = [];

  filtros: FiltrosReporte = {
    vendedores: [],
    zonas: [],
    productos: [],
    searchTerm: ''
  };

  isLoading = false;
  errorMessage: string | null = null;

  showVendedoresModal = false;
  showZonasModal = false;
  showProductosModal = false;

  pageSize = 10;
  currentPage = 1;

  estadisticas: EstadisticasReporte = {
    totalVentas: 0,
    cumplimientoPromedio: 0
  };

  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private planVentaService: PlanVentaService,
    private productService: ProductService,
    private vendedorService: VendedorService,
    private reporteService: ReporteService,
    private cdr: ChangeDetectorRef,
    private excelExport: ExcelExportService,
    private pdfExport: PdfExportService,
  ) { }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.cdr.detectChanges();

    forkJoin({
      planes: this.planVentaService.getPlanesVenta(),
      productos: this.productService.getAllProducts(),
      vendedores: this.vendedorService.getVendedores(),
      zonas: this.vendedorService.getZonas(),
      reportes: this.reporteService.getReportes()
    })
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          const planes: PlanVenta[] = data.planes.selling_plans || [];
          const reportes: ReporteData[] = data.reportes || [];

          // 🔹 1. Filtrar vendedores con selling plan
          const vendedorIdsConPlan = new Set(planes.map(p => p.seller?.id).filter(Boolean) as string[]);
          this.vendedores = (data.vendedores.sellers || []).filter(v => vendedorIdsConPlan.has(v.id!));

          // 🔹 2. Filtrar productos con selling plan
          const productoIdsConPlan = new Set(planes.map(p => p.product?.id).filter(Boolean) as string[]);
          this.productos = (data.productos.products || []).filter(p => productoIdsConPlan.has(p.id!));

          // 🔹 3. Filtrar zonas con selling plan
          const zonaIdsConPlan = new Set(planes.map(p => p.zone?.id).filter(Boolean) as string[]);
          this.zonas = (data.zonas || []).filter(z => zonaIdsConPlan.has(z.id!));

          // 🔹 4. Combinar planes con datos reales de ventas
          this.reportes = this.transformarPlanesAReportes(planes, reportes);
          this.reportesFiltrados = [...this.reportes];

          this.calcularEstadisticas();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cargar datos:', err);
          this.errorMessage = 'Error al cargar los datos. Intente nuevamente.';
          this.cdr.detectChanges();
        }
      });
  }

  transformarPlanesAReportes(planes: PlanVenta[], reportes: ReporteData[]): ReporteData[] {
    return planes.map(plan => {
      const meta = plan.goal || 0;
      const reporteRelacionado = reportes.find(r =>
        r.vendedor_id === plan.seller?.id &&
        r.producto_id === plan.product?.id
      );

      const ventas = reporteRelacionado?.ventas || 0;
      const porcentajeMeta = meta > 0 ? Math.round((ventas / meta) * 100) : 0;

      return {
        vendedor: plan.seller?.full_name || 'Sin vendedor',
        vendedor_id: plan.seller?.id || '',
        producto: plan.product?.name || 'Sin producto',
        producto_id: plan.product?.id || '',
        zona: (plan.zone as any)?.description || plan.zone?.description || 'Sin zona',
        zona_id: plan.zone?.id || '',
        meta,
        ventas,
        porcentajeMeta,
        periodo: plan.period
      } as ReporteData;
    });
  }

  aplicarFiltros(): void {
    this.reportesFiltrados = this.reportes.filter(reporte => {
      if (this.filtros.vendedores.length > 0 &&
        !this.filtros.vendedores.includes(reporte.vendedor_id)) {
        return false;
      }
      if (this.filtros.zonas.length > 0 &&
        !this.filtros.zonas.includes(reporte.zona_id)) {
        return false;
      }
      if (this.filtros.productos.length > 0 &&
        !this.filtros.productos.includes(reporte.producto_id)) {
        return false;
      }
      if (this.filtros.searchTerm) {
        const term = this.filtros.searchTerm.toLowerCase();
        return reporte.vendedor.toLowerCase().includes(term) ||
          reporte.producto.toLowerCase().includes(term) ||
          reporte.zona.toLowerCase().includes(term);
      }
      return true;
    });

    this.currentPage = 1;
    this.calcularEstadisticas();
    this.cdr.detectChanges();
  }

  calcularEstadisticas(): void {
    const visibles = this.reportesFiltrados;
    this.estadisticas.totalVentas = visibles.reduce((sum, r) => sum + (r.ventas || 0), 0);
    this.estadisticas.cumplimientoPromedio = visibles.length > 0
      ? Math.round(visibles.reduce((sum, r) => sum + (r.porcentajeMeta || 0), 0) / visibles.length)
      : 0;
    this.cdr.detectChanges();
  }

  toggleFiltroVendedor(vendedorId: string): void {
    const index = this.filtros.vendedores.indexOf(vendedorId);
    index > -1 ? this.filtros.vendedores.splice(index, 1) : this.filtros.vendedores.push(vendedorId);
    this.aplicarFiltros();
  }

  toggleFiltroZona(zonaId: string): void {
    const index = this.filtros.zonas.indexOf(zonaId);
    index > -1 ? this.filtros.zonas.splice(index, 1) : this.filtros.zonas.push(zonaId);
    this.aplicarFiltros();
  }

  toggleFiltroProducto(productoId: string): void {
    const index = this.filtros.productos.indexOf(productoId);
    index > -1 ? this.filtros.productos.splice(index, 1) : this.filtros.productos.push(productoId);
    this.aplicarFiltros();
  }

  onSearchChange(term: string): void {
    this.filtros.searchTerm = term;
    this.aplicarFiltros();
  }

  limpiarFiltros(): void {
    this.filtros = { vendedores: [], zonas: [], productos: [], searchTerm: '' };
    this.aplicarFiltros();
  }

  exportarExcel(): void {
    if (this.reportesFiltrados.length === 0) {
      this.showToast('No hay datos para exportar', 'error');
      return;
    }
    this.excelExport.exportarReportes(this.reportesFiltrados);
    this.showToast('Archivo Excel exportado exitosamente', 'success');
  }

  exportarPDF(): void {
    if (this.reportesFiltrados.length === 0) {
      this.showToast('No hay datos para exportar', 'error');
      return;
    }
    this.pdfExport.exportarReportes(this.reportesFiltrados);
    this.showToast('Archivo PDF generado exitosamente', 'success');
  }

  getProgressColor(porcentaje: number): string {
    if (porcentaje >= 70) return 'success';
    if (porcentaje >= 30) return '';
    return 'danger';
  }

  onPageSizeChange(event: Event): void {
    this.pageSize = Number((event.target as HTMLSelectElement).value);
    this.currentPage = 1;
    this.cdr.detectChanges();
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.cdr.detectChanges();
    }
  }

  nextPage(): void {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (this.currentPage < totalPages) {
      this.currentPage++;
      this.cdr.detectChanges();
    }
  }

  get reportesPaginados(): ReporteData[] {
    const start = (this.currentPage - 1) * this.pageSize;
    const end = start + this.pageSize;
    return this.reportesFiltrados.slice(start, end);
  }

  get startItem(): number {
    return this.totalItems > 0 ? (this.currentPage - 1) * this.pageSize + 1 : 0;
  }

  get endItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.totalItems);
  }

  get totalItems(): number {
    return this.reportesFiltrados.length;
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

  get vendedoresSeleccionadosCount(): number {
    return this.filtros.vendedores.length;
  }

  get zonasSeleccionadasCount(): number {
    return this.filtros.zonas.length;
  }

  get productosSeleccionadosCount(): number {
    return this.filtros.productos.length;
  }
}
