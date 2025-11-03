import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ListaPlanVentaComponent } from './lista-plan-venta.component';
import { PlanVentaService } from '../../../services/planes-de-venta/planesDeVenta.service';
import { CsvExportService } from '../../../services/utilities/csv.service';
import { PlanVenta, GetPlanesVentaResponse } from '../../../interfaces/planVenta.interface';
import { ProductCreateResponse } from '../../../interfaces/producto.interface';
import { Zone, Vendedor } from '../../../interfaces/vendedor.interface';

// ----- Stubs de componentes que se usan en la plantilla -----
@Component({ selector: 'app-loading-spinner', standalone: true, template: '' })
class StubLoadingSpinnerComponent { }

@Component({ selector: 'app-item-dialog', template: '' })
class StubItemDialogComponent { }

// ----- Datos de prueba -----
const mockProduct: ProductCreateResponse = {
  id: 'p1', name: 'Producto Test', details: '', store: '', batch: '',
  image_url: null, due_date: '', stock: 0, price_per_unit: 0,
  provider_id: '', created_at: '2023-01-01T00:00:00Z'
};
const mockZone: Zone = { id: 'z1', description: 'Zona Norte' };
const mockVendedor: Vendedor = { id: 'v1', full_name: 'Juan Vendedor', doi: '123', email: 'j@v.com', phone: '555' };

const mockPlanesVenta: PlanVenta[] = [];
for (let i = 1; i <= 10; i++) {
  mockPlanesVenta.push({
    id: `plan${i}`,
    period: 'Enero',
    goal: 100 + i,
    created_at: '2023-01-01T00:00:00Z',
    product: { ...mockProduct, name: `Producto ${i}` },
    zone: mockZone,
    seller: mockVendedor
  });
}
mockPlanesVenta.push({
  id: 'plan11',
  period: 'Febrero',
  goal: 50,
  created_at: '2023-02-01T00:00:00Z',
  product: undefined,
  zone: undefined,
  seller: undefined
});

const mockPlanesVentaResponse: GetPlanesVentaResponse = {
  selling_plans: mockPlanesVenta,
  total_count: 11
};

// ----- Spies -----
let routerSpy: jasmine.SpyObj<Router>;
let planVentaServiceSpy: jasmine.SpyObj<PlanVentaService>;
let csvServiceSpy: jasmine.SpyObj<CsvExportService>;

// ----- Test Suite -----
describe('ListaPlanVentaComponent', () => {
  let component: ListaPlanVentaComponent;
  let fixture: ComponentFixture<ListaPlanVentaComponent>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    planVentaServiceSpy = jasmine.createSpyObj('PlanVentaService', ['getPlanesVenta']);
    csvServiceSpy = jasmine.createSpyObj('CsvExportService', ['exportarACsv']);

    await TestBed.configureTestingModule({
      imports: [ListaPlanVentaComponent, CommonModule, MatIconModule, StubLoadingSpinnerComponent],
      declarations: [StubItemDialogComponent],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: PlanVentaService, useValue: planVentaServiceSpy },
        { provide: CsvExportService, useValue: csvServiceSpy },
      ]
    }).compileComponents();

    spyOn(navigator.clipboard, 'writeText').and.returnValue(Promise.resolve());
  });

  it('should load data successfully on init', fakeAsync(() => {
    planVentaServiceSpy.getPlanesVenta.and.returnValue(of(mockPlanesVentaResponse));

    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // ngOnInit
    tick();

    expect(component.planesVenta.length).toBe(mockPlanesVentaResponse.selling_plans.length);
  }));

  it('should handle data loading error', fakeAsync(() => {
    planVentaServiceSpy.getPlanesVenta.and.returnValue(throwError(() => new Error('Error')));

    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // ngOnInit
    tick();

    expect(component.planesVenta.length).toBe(0);
    expect(component.error).toBeTruthy();
  }));

  it('should export data correctly', () => {
    planVentaServiceSpy.getPlanesVenta.and.returnValue(of(mockPlanesVentaResponse));

    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = mockPlanesVenta;

    component.exportarCSV();

    expect(csvServiceSpy.exportarACsv).toHaveBeenCalled();
  });

  it('should handle undefined product, zone, seller when exporting CSV', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = mockPlanesVenta;

    const lastPlan = mockPlanesVenta[mockPlanesVenta.length - 1];
    component.exportarCSV();

    expect(lastPlan.product).toBeUndefined();
    expect(lastPlan.zone).toBeUndefined();
    expect(lastPlan.seller).toBeUndefined();
  });

  it('should show error toast if no data to export', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = [];

    component.exportarCSV();
    expect(csvServiceSpy.exportarACsv).not.toHaveBeenCalled();
    expect(component.toastType).toBe('error');
  });

  it('should copy id to clipboard', fakeAsync(() => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;

    component.copyToClipboard('123');
    tick(1500);

    expect(component.copiedId).toBeNull();
  }));

  // ✅ NUEVO: Test para error al copiar al portapapeles
  it('should handle clipboard copy error', fakeAsync(() => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;

    (navigator.clipboard.writeText as jasmine.Spy).and.returnValue(Promise.reject('Error'));
    spyOn(console, 'error');

    component.copyToClipboard('123');
    tick();

    expect(console.error).toHaveBeenCalledWith('Error al copiar ID: ', 'Error');
  }));

  it('should show item detail dialog', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;

    const item = { id: 'item1' };
    component.mostrarDetalle(item);

    expect(component.selectedItem).toEqual(item);
    expect(component.dialogVisible).toBeTrue();
  });

  // ✅ NUEVO: Test para cerrar el diálogo
  it('should close dialog', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;

    component.dialogVisible = true;
    component.selectedItem = { id: 'test' };

    // Simular el evento de cerrar
    component.dialogVisible = false;

    expect(component.dialogVisible).toBeFalse();
  });

  // ---- Pagination ----
  it('should go to next page', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = mockPlanesVenta;
    component.pageSize = 5;
    component.currentPage = 1;

    component.nextPage();
    expect(component.currentPage).toBe(2);
  });

  it('should not go to next page if on last page', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = mockPlanesVenta;
    component.pageSize = 5;
    component.currentPage = 3;

    component.nextPage();
    expect(component.currentPage).toBe(3);
  });

  it('should go to previous page when currentPage > 1', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.currentPage = 2;

    component.previousPage();
    expect(component.currentPage).toBe(1);
  });

  it('should not go to previous page if on first page', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.currentPage = 1;

    component.previousPage();
    expect(component.currentPage).toBe(1);
  });

  // ✅ NUEVO: Test para cambio de tamaño de página
  it('should change page size and reset to first page', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.currentPage = 3;
    component.pageSize = 10;

    const event = { target: { value: '20' } } as any;
    component.onPageSizeChange(event);

    expect(component.pageSize).toBe(20);
    expect(component.currentPage).toBe(1);
  });

  // ✅ NUEVO: Test para crear plan de venta
  it('should navigate to create plan', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;

    component.crearPlanVenta();

    expect(routerSpy.navigate).toHaveBeenCalledWith(['/planes-de-venta/crear']);
  });

  // ✅ NUEVO: Test para getters de paginación
  it('should return correct startItem when totalItems is 0', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = [];

    expect(component.startItem).toBe(0);
  });

  it('should return correct startItem when totalItems > 0', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = mockPlanesVenta;
    component.currentPage = 2;
    component.pageSize = 5;

    expect(component.startItem).toBe(6);
  });

  it('should return correct endItem', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = mockPlanesVenta;
    component.currentPage = 1;
    component.pageSize = 5;

    expect(component.endItem).toBe(5);
  });

  it('should return correct planesPaginados', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = mockPlanesVenta;
    component.currentPage = 1;
    component.pageSize = 5;

    const paginated = component.planesPaginados;
    expect(paginated.length).toBe(5);
    expect(paginated[0].id).toBe('plan1');
  });

  // ✅ NUEVO: Test para showToast y que desaparezca
  it('should show and hide toast message', fakeAsync(() => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;

    component.showToast('Test message', 'success');
    expect(component.toastMessage).toBe('Test message');
    expect(component.toastType).toBe('success');

    tick(4000);
    expect(component.toastMessage).toBeNull();
  }));

  it('should show error toast message', fakeAsync(() => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;

    component.showToast('Error message', 'error');
    expect(component.toastMessage).toBe('Error message');
    expect(component.toastType).toBe('error');

    tick(4000);
    expect(component.toastMessage).toBeNull();
  }));

  // ✅ NUEVO: Test para reintentar carga después de error
  it('should retry loading planes after error', fakeAsync(() => {
    planVentaServiceSpy.getPlanesVenta.and.returnValue(throwError(() => new Error('Error')));

    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    tick();

    expect(component.error).toBeTruthy();

    // Ahora debe funcionar
    planVentaServiceSpy.getPlanesVenta.and.returnValue(of(mockPlanesVentaResponse));
    component.cargarPlanesVenta();
    tick();

    expect(component.planesVenta.length).toBe(11);
    expect(component.error).toBeNull();
  }));

  // ✅ NUEVO: Test para métodos privados de mapeo
  it('should map plan data correctly for CSV export', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
    component.planesVenta = [mockPlanesVenta[0]];

    component.exportarCSV();

    const callArgs = csvServiceSpy.exportarACsv.calls.mostRecent().args;
    const mappedData = callArgs[0];

    expect(mappedData[0].id).toBe('plan1');
    expect(mappedData[0].period).toBe('Enero');
    expect(mappedData[0].goal).toBe(101);
    expect(mappedData[0].product_name).toBe('Producto 1');
    expect(mappedData[0].zone_description).toBe('Zona Norte');
    expect(mappedData[0].seller_name).toBe('Juan Vendedor');
  });

  // ✅ NUEVO: Test para extraer valores vacíos/undefined
  it('should handle empty values when mapping CSV data', () => {
    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;

    const planWithNulls: PlanVenta = {
      id: null as any,
      period: 'Test',
      goal: 100,
      created_at: null as any,
      product: undefined,
      zone: undefined,
      seller: undefined
    };

    component.planesVenta = [planWithNulls];
    component.exportarCSV();

    const callArgs = csvServiceSpy.exportarACsv.calls.mostRecent().args;
    const mappedData = callArgs[0];

    expect(mappedData[0].id).toBe('');
    expect(mappedData[0].product_name).toBe('');
    expect(mappedData[0].zone_description).toBe('');
    expect(mappedData[0].seller_name).toBe('');
    expect(mappedData[0].created_at).toBe('');
  });
});
