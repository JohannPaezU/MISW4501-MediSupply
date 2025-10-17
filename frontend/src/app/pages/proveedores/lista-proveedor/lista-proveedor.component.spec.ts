import { ComponentFixture, TestBed, fakeAsync, tick, flushMicrotasks } from '@angular/core/testing';
import { ListaProveedorComponent } from './lista-proveedor.component';
import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { CsvExportService } from '../../../services/utilities/csvExport.service';
import { of, throwError } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';

describe('ListaProveedorComponent', () => {
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

    expect(proveedorServiceSpy.getProviders).toHaveBeenCalled();
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
});
