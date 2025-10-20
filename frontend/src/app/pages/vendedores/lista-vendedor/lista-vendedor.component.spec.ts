import { ComponentFixture, TestBed, fakeAsync, tick, flush } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ListaVendedorComponent } from './lista-vendedor.component';
import { VendedorService } from '../../../services/vendedores/vendedor.service';
import { CsvExportService } from '../../../services/utilities/csv.service';
import { Vendedor, VendedorResponse, Zone } from '../../../interfaces/vendedor.interface';
import { HttpClientTestingModule } from '@angular/common/http/testing';


@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  template: ''
})
class StubLoadingSpinnerComponent { }

const mockZone: Zone = { id: 'z1', description: 'Zona Norte' };

const mockVendedores: Vendedor[] = [];
for (let i = 1; i <= 10; i++) {
  mockVendedores.push({
    id: `v${i}`,
    full_name: `Vendedor ${i}`,
    doi: `1000${i}`,
    email: `v${i}@test.com`,
    phone: `555000${i}`,
    created_at: '2023-01-01T00:00:00Z',
    zone: mockZone
  });
}
mockVendedores.push({
  id: 'v11',
  full_name: 'Vendedor 11 Sin Zona',
  doi: '11111',
  email: 'v11@test.com',
  phone: '5551111',
  created_at: '2023-02-01T00:00:00Z',
  zone: undefined
});

const mockVendedorResponse: VendedorResponse = {
  sellers: mockVendedores,
  total_count: 11
};

let routerSpy: jasmine.SpyObj<Router>;
let vendedorServiceSpy: jasmine.SpyObj<VendedorService>;
let csvServiceSpy: jasmine.SpyObj<CsvExportService>;


describe('ListaVendedorComponent', () => {
  let component: ListaVendedorComponent;
  let fixture: ComponentFixture<ListaVendedorComponent>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    vendedorServiceSpy = jasmine.createSpyObj('VendedorService', ['getVendedores']);
    csvServiceSpy = jasmine.createSpyObj('CsvExportService', ['exportarACsv']);

    await TestBed.configureTestingModule({
      imports: [
        ListaVendedorComponent,
        MatIconModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: VendedorService, useValue: vendedorServiceSpy },
        { provide: CsvExportService, useValue: csvServiceSpy },
      ]
    })
      .overrideComponent(ListaVendedorComponent, {
        set: {
          imports: [CommonModule, MatIconModule, StubLoadingSpinnerComponent]
        }
      })
      .compileComponents();

    vendedorServiceSpy.getVendedores.and.returnValue(of(mockVendedorResponse));

    spyOn(navigator.clipboard, 'writeText').and.returnValue(Promise.resolve());

    fixture = TestBed.createComponent(ListaVendedorComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  describe('Initialization and Data Loading', () => {
    it('should call cargarVendedores on init', () => {
      spyOn(component, 'cargarVendedores').and.callThrough();
      fixture.detectChanges();
      expect(component.cargarVendedores).toHaveBeenCalled();
    });

    it('should load sellers successfully on init', () => {
      fixture.detectChanges();

      expect(component.isLoading).toBeFalse();
      expect(vendedorServiceSpy.getVendedores).toHaveBeenCalled();
      expect(component.vendedores.length).toBe(11);
      expect(component.vendedoresFiltrados.length).toBe(11);
      expect(component.vendedores[0].id).toBe('v1');
      expect(component.error).toBeNull();
    });

    it('should handle data loading error', () => {
      vendedorServiceSpy.getVendedores.and.returnValue(throwError(() => new Error('Test Error')));
      spyOn(console, 'error');

      fixture.detectChanges();

      expect(component.isLoading).toBeFalse();
      expect(component.error).toBe('No se pudieron cargar los vendedores. Intente de nuevo más tarde.');
      expect(component.vendedores.length).toBe(0);
      expect(console.error).toHaveBeenCalled();
    });
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
      expect(component.vendedoresPaginados.length).toBe(10);
      expect(component.vendedoresPaginados[0].id).toBe('v1');
      expect(component.hasPreviousPage).toBeFalse();
      expect(component.hasNextPage).toBeTrue();
    });

    it('should navigate to next page and update getters', () => {
      component.nextPage();
      fixture.detectChanges();

      expect(component.currentPage).toBe(2);
      expect(component.vendedoresPaginados.length).toBe(1);
      expect(component.vendedoresPaginados[0].id).toBe('v11');
      expect(component.startItem).toBe(11);
      expect(component.endItem).toBe(11);
      expect(component.hasPreviousPage).toBeTrue();
      expect(component.hasNextPage).toBeFalse();
    });

    it('should not navigate past the last page', () => {
      component.nextPage();
      expect(component.currentPage).toBe(2);
      component.nextPage();
      expect(component.currentPage).toBe(2);
    });

    it('should navigate to previous page', () => {
      component.nextPage();
      component.previousPage();
      expect(component.currentPage).toBe(1);
    });

    it('should not navigate before the first page', () => {
      component.previousPage();
      expect(component.currentPage).toBe(1);
    });

    it('should update page size and reset current page', () => {
      component.nextPage();
      expect(component.currentPage).toBe(2);

      const fakeEvent = { target: { value: '5' } } as unknown as Event;
      component.onPageSizeChange(fakeEvent);
      fixture.detectChanges();

      expect(component.pageSize).toBe(5);
      expect(component.currentPage).toBe(1);
      expect(component.vendedoresPaginados.length).toBe(5);
    });
  });


  describe('CSV Export', () => {

    it('should export data correctly', fakeAsync(() => {
      fixture.detectChanges();
      component.exportarCSV();

      expect(csvServiceSpy.exportarACsv).toHaveBeenCalled();

      const [data, filename, headers] = csvServiceSpy.exportarACsv.calls.first().args;

      expect(data.length).toBe(11);
      expect(filename).toBe('vendedores');
      expect(headers).toBeDefined();
      if (headers) {
        expect(headers['full_name']).toBe('Nombre completo');
        expect(headers['zone_description']).toBe('Zona asignada');
      }

      expect(data[0].id).toBe('v1');
      expect(data[0].zone_description).toBe('Zona Norte');
      expect(data[10].id).toBe('v11');
      expect(data[10].zone_description).toBe('');

      expect(component.toastMessage).toBe('CSV exportado exitosamente.');

      flush();
    }));

    it('should show error toast if no data to export', fakeAsync(() => {
      fixture.detectChanges();
      component.vendedores = [];
      component.vendedoresFiltrados = [];
      fixture.detectChanges();

      component.exportarCSV();

      expect(csvServiceSpy.exportarACsv).not.toHaveBeenCalled();
      expect(component.toastMessage).toBe('No hay vendedores para exportar.');

      flush();
    }));
  });


  describe('Navigation and UI Helpers', () => {
    it('should navigate to create page', () => {
      component.crearVendedor();
      expect(routerSpy.navigate).toHaveBeenCalledWith(['/vendedores/crear']);
    });

    it('should copy ID to clipboard and reset', fakeAsync(() => {
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

  describe('Branches no cubiertos', () => {

    it('nextPage should not increment if already on last page', () => {
      fixture.detectChanges();
      component.currentPage = 2;
      component.nextPage();
      expect(component.currentPage).toBe(2);
    });

    it('startItem should return 0 if no items', () => {
      component.vendedoresFiltrados = [];
      expect(component.startItem).toBe(0);
    });

    it('copyToClipboard should handle error', fakeAsync(() => {
      (navigator.clipboard.writeText as jasmine.Spy).and.returnValue(Promise.reject('Error'));
      spyOn(console, 'error');
      component.copyToClipboard('fail-id');
      tick();
      expect(console.error).toHaveBeenCalledWith('Error al copiar ID: ', 'Error');
      expect(component.copiedId).toBeNull();
    }));

  });
});
