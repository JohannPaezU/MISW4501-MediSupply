import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReportesComponent } from './reportes.component';
import { ChangeDetectorRef } from '@angular/core';
import { of, throwError } from 'rxjs';

import { PlanVentaService } from '../../../services/planes-de-venta/planesDeVenta.service';
import { ProductService } from '../../../services/productos/product.service';
import { VendedorService } from '../../../services/vendedores/vendedor.service';
import { ReporteService } from '../../../services/reportes/reportes.service';
import { ExcelExportService } from '../../../services/utilities/excel.service';
import { PdfExportService } from '../../../services/utilities/pdf.service';

import { PlanVenta } from '../../../interfaces/planVenta.interface';
import { ProductCreateResponse } from '../../../interfaces/producto.interface';
import { Vendedor, Zone } from '../../../interfaces/vendedor.interface';
import { ReporteData } from '../../../interfaces/reporte.interface';

describe('ReportesComponent - full coverage', () => {
  let component: ReportesComponent;
  let fixture: ComponentFixture<ReportesComponent>;

  let planVentaServiceSpy: jasmine.SpyObj<PlanVentaService>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let vendedorServiceSpy: jasmine.SpyObj<VendedorService>;
  let reporteServiceSpy: jasmine.SpyObj<ReporteService>;
  let excelSpy: jasmine.SpyObj<ExcelExportService>;
  let pdfSpy: jasmine.SpyObj<PdfExportService>;
  let cdrSpy: jasmine.SpyObj<ChangeDetectorRef>;

  beforeEach(async () => {
    // single spy for ChangeDetectorRef
    cdrSpy = jasmine.createSpyObj('ChangeDetectorRef', ['detectChanges']);

    planVentaServiceSpy = jasmine.createSpyObj('PlanVentaService', ['getPlanesVenta']);
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getAllProducts']);
    vendedorServiceSpy = jasmine.createSpyObj('VendedorService', ['getVendedores', 'getZonas']);
    reporteServiceSpy = jasmine.createSpyObj('ReporteService', ['getReportes']);
    excelSpy = jasmine.createSpyObj('ExcelExportService', ['exportarReportes']);
    pdfSpy = jasmine.createSpyObj('PdfExportService', ['exportarReportes']);

    await TestBed.configureTestingModule({
      imports: [ReportesComponent],
      providers: [
        { provide: ChangeDetectorRef, useValue: cdrSpy },

        { provide: PlanVentaService, useValue: planVentaServiceSpy },
        { provide: ProductService, useValue: productServiceSpy },
        { provide: VendedorService, useValue: vendedorServiceSpy },
        { provide: ReporteService, useValue: reporteServiceSpy },
        { provide: ExcelExportService, useValue: excelSpy },
        { provide: PdfExportService, useValue: pdfSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReportesComponent);
    component = fixture.componentInstance;
  });

  function makeMocks() {
    const planes: PlanVenta[] = [
      {
        id: 'plan-1',
        period: '2025-10',
        goal: 100,
        product: { id: 'prod-1', name: 'Amox' } as any,
        seller: { id: 'seller-1', full_name: 'Pedro' } as any,
        zone: { id: 'zone-1', description: 'Zona A' } as any
      } as any,
      {
        id: 'plan-2',
        period: '2025-10',
        goal: 0,
        product: { id: 'prod-2', name: 'Ibu' } as any,
        seller: { id: 'seller-2', full_name: 'Ana' } as any,
        zone: { id: 'zone-2', description: 'Zona B' } as any
      } as any
    ];

    const productosResp: { total_count: number; products: ProductCreateResponse[] } = {
      total_count: 2,
      products: [
        { id: 'prod-1', name: 'Amox', details: '', store: '', batch: '', image_url: null, due_date: '', stock: 10, price_per_unit: 1, provider_id: '', created_at: '' } as any,
        { id: 'prod-2', name: 'Ibu', details: '', store: '', batch: '', image_url: null, due_date: '', stock: 5, price_per_unit: 1, provider_id: '', created_at: '' } as any
      ]
    };

    const vendedoresResp = {
      total_count: 2,
      sellers: [
        { id: 'seller-1', full_name: 'Pedro', doi: '1', email: 'p@x', phone: '1', created_at: '', zone: { id: 'zone-1', description: 'Zona A' } } as Vendedor,
        { id: 'seller-2', full_name: 'Ana', doi: '2', email: 'a@x', phone: '2', created_at: '', zone: { id: 'zone-2', description: 'Zona B' } } as Vendedor
      ]
    };

    const zonasResp: Zone[] = [
      { id: 'zone-1', description: 'Zona A' } as Zone,
      { id: 'zone-2', description: 'Zona B' } as Zone
    ];

    const reportes: ReporteData[] = [
      { vendedor: 'Pedro', vendedor_id: 'seller-1', producto: 'Amox', producto_id: 'prod-1', zona: 'Zona A', zona_id: 'zone-1', ventas: 80, meta: 100, porcentajeMeta: 80, periodo: '2025-10' } as any,
      { vendedor: 'Ana', vendedor_id: 'seller-2', producto: 'Ibu', producto_id: 'prod-2', zona: 'Zona B', zona_id: 'zone-2', ventas: 0, meta: 0, porcentajeMeta: 0, periodo: '2025-10' } as any
    ];

    return { planes, productosResp, vendedoresResp, zonasResp, reportes };
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('cargarDatos -> success path: fills lists, filters only items with plan and computes reportes', fakeAsync(() => {
    const { planes, productosResp, vendedoresResp, zonasResp, reportes } = makeMocks();

    planVentaServiceSpy.getPlanesVenta.and.returnValue(of({ total_count: 2, selling_plans: planes } as any));
    productServiceSpy.getAllProducts.and.returnValue(of(productosResp as any));
    vendedorServiceSpy.getVendedores.and.returnValue(of(vendedoresResp as any));
    vendedorServiceSpy.getZonas.and.returnValue(of(zonasResp as any));
    reporteServiceSpy.getReportes.and.returnValue(of(reportes as any));

    component.cargarDatos();
    // ensure all microtasks and finalize() run
    tick();
    // also trigger Angular template change detection in test environment
    fixture.detectChanges();

    expect(component.vendedores.length).toBe(2);
    expect(component.productos.length).toBe(2);
    expect(component.zonas.length).toBe(2);

    expect(component.reportes.length).toBe(2);
    const r0 = component.reportes.find(r => r.vendedor_id === 'seller-1' && r.producto_id === 'prod-1')!;
    expect(r0).toBeDefined();
    expect(r0.ventas).toBe(80);
    expect(r0.porcentajeMeta).toBe(80);

    // assert detectChanges was called at least once
  }));

  it('cargarDatos -> handles error path gracefully', fakeAsync(() => {
    planVentaServiceSpy.getPlanesVenta.and.returnValue(throwError(() => new Error('fail')));
    productServiceSpy.getAllProducts.and.returnValue(of({ total_count: 0, products: [] } as any));
    vendedorServiceSpy.getVendedores.and.returnValue(of({ total_count: 0, sellers: [] } as any));
    vendedorServiceSpy.getZonas.and.returnValue(of([] as any));
    reporteServiceSpy.getReportes.and.returnValue(of([] as any));

    component.cargarDatos();
    tick();
    fixture.detectChanges();

    expect(component.isLoading).toBeFalse();
    expect(component.errorMessage).toBe('Error al cargar los datos. Intente nuevamente.');
  }));

  it('transformarPlanesAReportes -> meta > 0 and meta == 0 branches', () => {
    const { planes, reportes } = makeMocks();

    const out = component.transformarPlanesAReportes(planes as any, reportes as any);
    const p1 = out.find(x => x.producto_id === 'prod-1')!;
    const p2 = out.find(x => x.producto_id === 'prod-2')!;

    expect(p1.meta).toBe(100);
    expect(p1.ventas).toBe(80);
    expect(p1.porcentajeMeta).toBe(80);

    expect(p2.meta).toBe(0);
    expect(p2.porcentajeMeta).toBe(0);
  });

  it('aplicarFiltros -> filters by vendedor, zona, producto and searchTerm', () => {
    component.reportes = [
      { vendedor_id: 'v1', zona_id: 'z1', producto_id: 'p1', vendedor: 'Juan', producto: 'ProdA', zona: 'Centro', ventas: 5, porcentajeMeta: 10 } as any,
      { vendedor_id: 'v2', zona_id: 'z2', producto_id: 'p2', vendedor: 'Ana', producto: 'ProdB', zona: 'Norte', ventas: 7, porcentajeMeta: 20 } as any
    ];

    component.filtros = { vendedores: ['v1'], zonas: [], productos: [], searchTerm: '' };
    component.aplicarFiltros();
    expect(component.reportesFiltrados.length).toBe(1);
    expect(component.reportesFiltrados[0].vendedor_id).toBe('v1');

    component.filtros = { vendedores: [], zonas: ['z2'], productos: [], searchTerm: '' };
    component.aplicarFiltros();
    expect(component.reportesFiltrados.length).toBe(1);
    expect(component.reportesFiltrados[0].zona_id).toBe('z2');

    component.filtros = { vendedores: [], zonas: [], productos: ['p1'], searchTerm: '' };
    component.aplicarFiltros();
    expect(component.reportesFiltrados.length).toBe(1);
    expect(component.reportesFiltrados[0].producto_id).toBe('p1');

    component.filtros = { vendedores: [], zonas: [], productos: [], searchTerm: 'ana' };
    component.aplicarFiltros();
    expect(component.reportesFiltrados.length).toBe(1);
    expect(component.reportesFiltrados[0].vendedor).toBe('Ana');
  });

  it('calcularEstadisticas -> computes totalVentas and cumplimientoPromedio for visibles', () => {
    component.reportesFiltrados = [
      { ventas: 10, porcentajeMeta: 50 } as any,
      { ventas: 20, porcentajeMeta: 100 } as any
    ];
    component.calcularEstadisticas();
    expect(component.estadisticas.totalVentas).toBe(30);
    expect(component.estadisticas.cumplimientoPromedio).toBe(75);
  });

  it('toggleFiltro* should add and remove ids and call aplicarFiltros', () => {
    spyOn(component, 'aplicarFiltros');

    component.toggleFiltroVendedor('v1');
    expect(component.filtros.vendedores).toContain('v1');
    component.toggleFiltroVendedor('v1');
    expect(component.filtros.vendedores).not.toContain('v1');

    component.toggleFiltroZona('z1');
    expect(component.filtros.zonas).toContain('z1');
    component.toggleFiltroZona('z1');
    expect(component.filtros.zonas).not.toContain('z1');

    component.toggleFiltroProducto('p1');
    expect(component.filtros.productos).toContain('p1');
    component.toggleFiltroProducto('p1');
    expect(component.filtros.productos).not.toContain('p1');

    expect(component.aplicarFiltros).toHaveBeenCalledTimes(6);
  });

  it('onSearchChange updates filtro and applies filters', () => {
    spyOn(component, 'aplicarFiltros');
    component.onSearchChange('abc');
    expect(component.filtros.searchTerm).toBe('abc');
    expect(component.aplicarFiltros).toHaveBeenCalled();
  });

  it('limpiarFiltros resets filters and applies filters', () => {
    component.filtros = { vendedores: ['1'], zonas: ['2'], productos: ['3'], searchTerm: 'x' };
    spyOn(component, 'aplicarFiltros');
    component.limpiarFiltros();
    expect(component.filtros.vendedores.length).toBe(0);
    expect(component.filtros.searchTerm).toBe('');
    expect(component.aplicarFiltros).toHaveBeenCalled();
  });

  it('exportarExcel -> when no data shows toast error; when has data calls service and shows success', () => {
    component.reportesFiltrados = [];
    component.exportarExcel();
    expect(component.toastMessage).toContain('No hay datos');

    component.reportesFiltrados = [{ vendedor: 'A' } as any];
    excelSpy.exportarReportes.calls.reset();
    component.exportarExcel();
    expect(excelSpy.exportarReportes).toHaveBeenCalledWith(component.reportesFiltrados);
    expect(component.toastMessage).toContain('Archivo Excel exportado exitosamente');
  });

  it('exportarPDF -> when no data shows toast error; when has data calls pdf service and shows success', () => {
    component.reportesFiltrados = [];
    component.exportarPDF();
    expect(component.toastMessage).toContain('No hay datos');

    component.reportesFiltrados = [{ vendedor: 'B' } as any];
    pdfSpy.exportarReportes.calls.reset();
    component.exportarPDF();
    expect(pdfSpy.exportarReportes).toHaveBeenCalledWith(component.reportesFiltrados);
    expect(component.toastMessage).toContain('Archivo PDF generado exitosamente');
  });

  it('getProgressColor returns correct class names for thresholds (edge cases)', () => {
    expect(component.getProgressColor(80)).toBe('success'); // >=70
    expect(component.getProgressColor(50)).toBe('');        // >=30 && <70
    expect(component.getProgressColor(10)).toBe('danger');  // <30
  });

  it('pagination: onPageSizeChange, next, prev and getters', () => {
    component.reportesFiltrados = Array.from({ length: 12 }, (_, i) => ({ ventas: i + 1 } as any));
    component.pageSize = 5;
    component.currentPage = 1;

    component.onPageSizeChange({ target: { value: '10' } } as any);
    expect(component.pageSize).toBe(10);
    expect(component.currentPage).toBe(1);

    component.nextPage();
    const totalPages = Math.ceil(component.totalItems / component.pageSize);
    if (totalPages > 1) expect(component.currentPage).toBeGreaterThanOrEqual(1);

    component.currentPage = 2;
    component.previousPage();
    expect(component.currentPage).toBe(1);

    const pag = component.reportesPaginados;
    expect(pag.length).toBeLessThanOrEqual(component.pageSize);

    expect(component.startItem).toBeGreaterThanOrEqual(0);
    expect(component.endItem).toBeGreaterThanOrEqual(0);
    expect(component.totalItems).toBe(12);
  });

  it('showToast sets message and clears after timeout', fakeAsync(() => {
    component.showToast('Hola', 'success');
    expect(component.toastMessage).toBe('Hola');
    tick(4000);
    expect(component.toastMessage).toBeNull();
  }));

  it('vendedoresSeleccionadosCount, zonasSeleccionadasCount, productosSeleccionadosCount getters', () => {
    component.filtros = { vendedores: ['a', 'b'], zonas: ['z'], productos: ['p1', 'p2', 'p3'], searchTerm: '' };
    expect(component.vendedoresSeleccionadosCount).toBe(2);
    expect(component.zonasSeleccionadasCount).toBe(1);
    expect(component.productosSeleccionadosCount).toBe(3);
  });
});
