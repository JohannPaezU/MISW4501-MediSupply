import { ComponentFixture, TestBed, fakeAsync, tick, flushMicrotasks } from '@angular/core/testing';
import { ListaProveedorComponent } from './lista-proveedor.component';
import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { CsvExportService } from '../../../services/utilities/csv.service';
import { of, throwError } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';

describe('ListaProveedorComponent (100% coverage)', () => {
  let component: ListaProveedorComponent;
  let fixture: ComponentFixture<ListaProveedorComponent>;
  let proveedorServiceSpy: jasmine.SpyObj<ProveedorService>;
  let csvExportSpy: jasmine.SpyObj<CsvExportService>;
  let cdrSpy: jasmine.SpyObj<ChangeDetectorRef>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    proveedorServiceSpy = jasmine.createSpyObj('ProveedorService', ['getProviders']);
    csvExportSpy = jasmine.createSpyObj('CsvExportService', ['exportarACsv']);
    cdrSpy = jasmine.createSpyObj('ChangeDetectorRef', ['detectChanges']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ListaProveedorComponent],
      providers: [
        { provide: ProveedorService, useValue: proveedorServiceSpy },
        { provide: CsvExportService, useValue: csvExportSpy },
        { provide: ChangeDetectorRef, useValue: cdrSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListaProveedorComponent);
    component = fixture.componentInstance;
    component['cdr'] = cdrSpy;
  });

  it('debería cargar los proveedores correctamente', fakeAsync(() => {
    const mockResponse = {
      total_count: 2,
      providers: [
        { id: '1', name: 'Proveedor A', rit: '123', city: 'Medellín', country: 'Colombia', image_url: null, email: 'a@test.com', phone: '123', created_at: '2024-01-01' },
        { id: '2', name: 'Proveedor B', rit: '456', city: 'Bogotá', country: 'Colombia', image_url: null, email: 'b@test.com', phone: '456', created_at: '2024-02-01' }
      ]
    };
    proveedorServiceSpy.getProviders.and.returnValue(of(mockResponse));

    component.cargarProveedores();
    flushMicrotasks();
    tick();

    expect(proveedorServiceSpy.getProviders).toHaveBeenCalled();
    expect(component.proveedores.length).toBe(2);
    expect(component.totalItems).toBe(2);
    expect(component.isLoading).toBeFalse();
    expect(cdrSpy.detectChanges).toHaveBeenCalled();
  }));

  it('debería manejar error al cargar proveedores', fakeAsync(() => {
    proveedorServiceSpy.getProviders.and.returnValue(throwError(() => new Error('Error en API')));

    component.cargarProveedores();
    flushMicrotasks();
    tick();

    expect(component.errorMessage).toContain('No se pudieron cargar');
    expect(component.isLoading).toBeFalse();
    expect(cdrSpy.detectChanges).toHaveBeenCalled();
  }));

  it('debería copiar al portapapeles y limpiar copiedId luego de 1.5s', fakeAsync(() => {
    const writeTextSpy = spyOn(navigator.clipboard, 'writeText').and.returnValue(Promise.resolve());

    component.copyToClipboard('abc123');
    flushMicrotasks();
    tick();

    expect(writeTextSpy).toHaveBeenCalledWith('abc123');
    expect(component.copiedId).toBe('abc123');
    expect(cdrSpy.detectChanges).toHaveBeenCalledTimes(1);

    tick(1500);
    expect(component.copiedId).toBeNull();
    expect(cdrSpy.detectChanges).toHaveBeenCalledTimes(2);
  }));

  it('debería manejar error al copiar al portapapeles', fakeAsync(() => {
    const writeTextSpy = spyOn(navigator.clipboard, 'writeText')
      .and.returnValue(Promise.reject('Error al copiar'));

    spyOn(console, 'error');
    component.copyToClipboard('xyz');
    flushMicrotasks();
    tick();

    expect(writeTextSpy).toHaveBeenCalled();
    expect(console.error).toHaveBeenCalledWith('Error al copiar ID: ', 'Error al copiar');
  }));

  it('debería exportar CSV correctamente', () => {
    component.proveedores = [
      { id: '1', name: 'Proveedor A', rit: '123', city: 'Medellín', country: 'Colombia', image_url: null, email: 'a@test.com', phone: '123', created_at: '2024-01-01' }
    ];
    component.exportarCSV();
    expect(csvExportSpy.exportarACsv).toHaveBeenCalled();
  });

  it('debería navegar a crear proveedor', () => {
    component.crearProveedor();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/proveedores/crear']);
  });

  it('debería cambiar el tamaño de página con onPageSizeChange', () => {
    const mockEvent = { target: { value: '20' } } as unknown as Event;
    component.pageSize = 10;
    component.currentPage = 3;
    component.onPageSizeChange(mockEvent);
    expect(component.pageSize).toBe(20);
    expect(component.currentPage).toBe(1);
  });

  it('debería retroceder página si currentPage > 1', () => {
    component.currentPage = 2;
    component.previousPage();
    expect(component.currentPage).toBe(1);
  });

  it('no debería retroceder página si currentPage = 1', () => {
    component.currentPage = 1;
    component.previousPage();
    expect(component.currentPage).toBe(1);
  });

  it('debería avanzar de página si currentPage < totalPages', () => {
    component.pageSize = 5;
    component.totalItems = 10;
    component.currentPage = 1;
    component.nextPage();
    expect(component.currentPage).toBe(2);
  });

  it('no debería avanzar de página si está en la última', () => {
    component.pageSize = 5;
    component.totalItems = 10;
    component.currentPage = 2;
    component.nextPage();
    expect(component.currentPage).toBe(2);
  });

  it('debería calcular paginatedProveedores correctamente', () => {
    component.pageSize = 2;
    component.currentPage = 1;
    component.proveedores = [
      { id: '1', name: 'A' } as any,
      { id: '2', name: 'B' } as any,
      { id: '3', name: 'C' } as any
    ];
    const result = component.paginatedProveedores;
    expect(result.length).toBe(2);
    expect(result[0].id).toBe('1');
  });

  it('debería calcular startItem y endItem correctamente', () => {
    component.pageSize = 10;
    component.currentPage = 2;
    component.totalItems = 25;
    component.proveedores = Array(20).fill({}) as any;
    expect(component.startItem).toBe(11);
    expect(component.endItem).toBe(20);
  });

  it('debería retornar startItem = 0 cuando no hay proveedores', () => {
    component.proveedores = [];
    expect(component.startItem).toBe(0);
  });

  it('debería retornar endItem = totalItems si excede el límite', () => {
    component.pageSize = 10;
    component.currentPage = 3;
    component.totalItems = 25;
    component.proveedores = Array(25).fill({}) as any;
    expect(component.endItem).toBe(25);
  });
});
