import { ComponentFixture, TestBed, fakeAsync, tick, flush } from '@angular/core/testing';
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


@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  template: ''
})
class StubLoadingSpinnerComponent {}

const mockProduct: ProductCreateResponse = {
  id: 'p1', name: 'Producto Test', details: '', store: '', batch: '',
  image_url: null, due_date: '', stock: 0, price_per_unite: 0,
  provider_id: '', created_at: '2023-01-01T00:00:00Z'
};
const mockZone: Zone = { id: 'z1', description: 'Zona Norte' };
const mockVendedor: Vendedor = {
  id: 'v1', full_name: 'Juan Vendedor', doi: '123',
  email: 'j@v.com', phone: '555'
};

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

let routerSpy: jasmine.SpyObj<Router>;
let planVentaServiceSpy: jasmine.SpyObj<PlanVentaService>;
let csvServiceSpy: jasmine.SpyObj<CsvExportService>;


describe('ListaPlanVentaComponent', () => {
  let component: ListaPlanVentaComponent;
  let fixture: ComponentFixture<ListaPlanVentaComponent>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    planVentaServiceSpy = jasmine.createSpyObj('PlanVentaService', ['getPlanesVenta']);
    csvServiceSpy = jasmine.createSpyObj('CsvExportService', ['exportarACsv']);

    await TestBed.configureTestingModule({
      imports: [
        ListaPlanVentaComponent,
        MatIconModule,
      ],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: PlanVentaService, useValue: planVentaServiceSpy },
        { provide: CsvExportService, useValue: csvServiceSpy },
      ]
    })
    .overrideComponent(ListaPlanVentaComponent, {
      set: {
        imports: [CommonModule, MatIconModule, StubLoadingSpinnerComponent]
      }
    })
    .compileComponents();

    planVentaServiceSpy.getPlanesVenta.and.returnValue(of(mockPlanesVentaResponse));

    spyOn(navigator.clipboard, 'writeText').and.returnValue(Promise.resolve());

    fixture = TestBed.createComponent(ListaPlanVentaComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  describe('Initialization and Data Loading', () => {
    it('should load data successfully on init', () => {
      fixture.detectChanges();

      expect(component.isLoading).toBeFalse();
      expect(planVentaServiceSpy.getPlanesVenta).toHaveBeenCalled();
      expect(component.planesVenta.length).toBe(11);
      expect(component.error).toBeNull();
    });

    it('should handle data loading error', fakeAsync(() => {
      planVentaServiceSpy.getPlanesVenta.and.returnValue(throwError(() => new Error('Test Error')));
      spyOn(console, 'error');

      fixture.detectChanges();

      expect(component.isLoading).toBeFalse();
      expect(component.error).toBe('No se pudieron cargar los planes de venta. Intente de nuevo más tarde.');

    }));
  });


  describe('Pagination', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should have correct initial pagination state', () => {
      expect(component.totalItems).toBe(11);
      expect(component.pageSize).toBe(10);
      expect(component.currentPage).toBe(1);
    });

    it('getters should calculate correctly for page 1', () => {
      expect(component.startItem).toBe(1);
      expect(component.endItem).toBe(10);
      expect(component.planesPaginados.length).toBe(10);
      expect(component.planesPaginados[0].id).toBe('plan1');
    });

    it('should navigate to next page', () => {
      component.nextPage();
      fixture.detectChanges();

      expect(component.currentPage).toBe(2);
      expect(component.startItem).toBe(11);
      expect(component.endItem).toBe(11);
      expect(component.planesPaginados.length).toBe(1);
      expect(component.planesPaginados[0].id).toBe('plan11');
    });

    it('should not go to previous page if on first page', () => {
      component.currentPage = 1;
      fixture.detectChanges();

      component.previousPage();
      fixture.detectChanges();

      expect(component.currentPage).toBe(1);
    });

    it('should not go to next page if on last page', () => {
      component.currentPage = 2;
      fixture.detectChanges();

      component.nextPage();
      fixture.detectChanges();

      expect(component.currentPage).toBe(2);
    });
  });

  describe('CSV Export', () => {
    it('should export data correctly', fakeAsync(() => {
      fixture.detectChanges();
      component.exportarCSV();

      expect(csvServiceSpy.exportarACsv).toHaveBeenCalled();

      const [data, filename, headers] = csvServiceSpy.exportarACsv.calls.first().args;

      expect(data.length).toBe(11);
      expect(filename).toBe('planes_venta');

      expect(headers).toBeDefined();
      if (headers) {
        expect(headers['product_name']).toBe('Producto');
      }


      expect(component.toastMessage).toBe('Planes de venta exportados correctamente.');
      flush();
    }));

    it('should show error toast if no data to export', fakeAsync(() => {
      fixture.detectChanges();

      component.planesVenta = [];

      fixture.detectChanges();

      component.exportarCSV();

      expect(csvServiceSpy.exportarACsv).not.toHaveBeenCalled();
      expect(component.toastMessage).toBe('No hay planes de venta para exportar.');

      flush();
    }));
  });

  describe('Navigation and UI Helpers', () => {
    it('should navigate to create page', () => {
      component.crearPlanVenta();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/planes-de-venta/crear']);
    });

    it('should copy ID to clipboard and show feedback', fakeAsync(() => {
      component.copyToClipboard('test-id-123');

      tick();

      expect(navigator.clipboard.writeText).toHaveBeenCalledWith('test-id-123');
      expect(component.copiedId).toBe('test-id-123');

      tick(1500);
      expect(component.copiedId).toBeNull();
    }));

    it('should show and hide toast message', fakeAsync(() => {
      component.showToast('Test Toast', 'error');

      expect(component.toastMessage).toBe('Test Toast');
      expect(component.toastType).toBe('error');

      tick(4000);

      expect(component.toastMessage).toBeNull();
    }));
  });
});
