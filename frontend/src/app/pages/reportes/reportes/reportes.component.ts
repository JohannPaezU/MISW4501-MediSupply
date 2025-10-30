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

  // Datos para filtros
  vendedores: Vendedor[] = [];
  zonas: Zone[] = [];
  productos: ProductCreateResponse[] = [];

  // Filtros seleccionados
  filtros: FiltrosReporte = {
    vendedores: [],
    zonas: [],
    productos: [],
    searchTerm: ''
  };

  // Estados UI
  isLoading = false;
  errorMessage: string | null = null;

  // Modales de filtros
  showVendedoresModal = false;
  showZonasModal = false;
  showProductosModal = false;

  // Paginación
  pageSize = 10;
  currentPage = 1;

  // Estadísticas
  estadisticas: EstadisticasReporte = {
    totalVentas: 0,
    pedidosPendientes: 23, // Mock - no hay endpoint
    cumplimientoPromedio: 0
  };

  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private planVentaService: PlanVentaService,
    private productService: ProductService,
    private vendedorService: VendedorService,
    private cdr: ChangeDetectorRef
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
      zonas: this.vendedorService.getZonas()
    })
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          this.productos = data.productos.products;
          this.vendedores = data.vendedores.sellers;
          this.zonas = data.zonas;

          // Transformar planes de venta en reportes
          this.reportes = this.transformarPlanesAReportes(data.planes.selling_plans);
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

  transformarPlanesAReportes(planes: PlanVenta[]): ReporteData[] {
    return planes.map(plan => {
      // Mock de ventas - calcular aleatoriamente basado en la meta
      const ventas = Math.floor(plan.goal * (Math.random() * 0.9 + 0.1));
      const porcentajeMeta = (ventas / plan.goal) * 100;

      return {
        vendedor: plan.seller?.full_name || 'Sin vendedor',
        vendedor_id: plan.seller?.id || '',
        producto: plan.product?.name || 'Sin producto',
        producto_id: plan.product?.id || '',
        zona: plan.zone?.description || 'Sin zona',
        zona_id: plan.zone?.id || '',
        meta: plan.goal,
        ventas: ventas,
        porcentajeMeta: Math.round(porcentajeMeta),
        periodo: plan.period
      };
    });
  }

  aplicarFiltros(): void {
    this.reportesFiltrados = this.reportes.filter(reporte => {
      // Filtro por vendedores
      if (this.filtros.vendedores.length > 0 &&
        !this.filtros.vendedores.includes(reporte.vendedor_id)) {
        return false;
      }

      // Filtro por zonas
      if (this.filtros.zonas.length > 0 &&
        !this.filtros.zonas.includes(reporte.zona_id)) {
        return false;
      }

      // Filtro por productos
      if (this.filtros.productos.length > 0 &&
        !this.filtros.productos.includes(reporte.producto_id)) {
        return false;
      }

      // Filtro por búsqueda
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
    const reportesActivos = this.reportesFiltrados;

    this.estadisticas.totalVentas = reportesActivos.reduce((sum, r) => sum + r.ventas, 0);
    this.estadisticas.cumplimientoPromedio = reportesActivos.length > 0
      ? Math.round(reportesActivos.reduce((sum, r) => sum + r.porcentajeMeta, 0) / reportesActivos.length)
      : 0;
  }

  toggleFiltroVendedor(vendedorId: string): void {
    const index = this.filtros.vendedores.indexOf(vendedorId);
    if (index > -1) {
      this.filtros.vendedores.splice(index, 1);
    } else {
      this.filtros.vendedores.push(vendedorId);
    }
    this.aplicarFiltros();
  }

  toggleFiltroZona(zonaId: string): void {
    const index = this.filtros.zonas.indexOf(zonaId);
    if (index > -1) {
      this.filtros.zonas.splice(index, 1);
    } else {
      this.filtros.zonas.push(zonaId);
    }
    this.aplicarFiltros();
  }

  toggleFiltroProducto(productoId: string): void {
    const index = this.filtros.productos.indexOf(productoId);
    if (index > -1) {
      this.filtros.productos.splice(index, 1);
    } else {
      this.filtros.productos.push(productoId);
    }
    this.aplicarFiltros();
  }

  onSearchChange(term: string): void {
    this.filtros.searchTerm = term;
    this.aplicarFiltros();
  }

  limpiarFiltros(): void {
    this.filtros = {
      vendedores: [],
      zonas: [],
      productos: [],
      searchTerm: ''
    };
    this.aplicarFiltros();
  }

  exportarExcel(): void {
    const headers = ['Vendedor', 'Producto', 'Zona', 'Meta', 'Ventas', '% Meta', 'Periodo'];
    const rows = this.reportesFiltrados.map(r => [
      r.vendedor,
      r.producto,
      r.zona,
      r.meta,
      r.ventas,
      `${r.porcentajeMeta}%`,
      r.periodo
    ]);

    const csvContent = [headers, ...rows].map(r => r.join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'reportes.csv';
    link.click();

    this.showToast('Excel exportado exitosamente', 'success');
  }

  exportarPDF(): void {
    this.showToast('Exportación a PDF próximamente disponible', 'error');
  }

  getProgressColor(porcentaje: number): string {
    if (porcentaje >= 70) return 'success';
    if (porcentaje >= 30) return '';
    return 'danger';
  }

  // Paginación
  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize = Number(target.value);
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
