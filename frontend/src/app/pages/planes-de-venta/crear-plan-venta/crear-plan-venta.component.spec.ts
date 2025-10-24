import { ComponentFixture, TestBed, fakeAsync, tick, flushMicrotasks, flush } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { of, throwError } from 'rxjs';
import { CrearPlanVentaComponent } from './crear-plan-venta.component';
import { PlanVentaService } from '../../../services/planes-de-venta/planesDeVenta.service';
import { ProductService } from '../../../services/productos/product.service';
import { VendedorService } from '../../../services/vendedores/vendedor.service';
import { ProductCreateResponse } from '../../../interfaces/producto.interface';
import { Zone, Vendedor } from '../../../interfaces/vendedor.interface';
import { CreatePlanVentaResponse } from '../../../interfaces/planVenta.interface';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  template: ''
})
class StubLoadingSpinnerComponent { }

const mockProductos: ProductCreateResponse[] = [
  {
    id: 'p1',
    name: 'Producto A',
    details: 'Detalle A',
    store: 'S1',
    batch: 'B1',
    image_url: null,
    due_date: '2025-12-31',
    stock: 100,
    price_per_unit: 10,
    provider_id: 'prov1',
    created_at: '2025-01-01T00:00:00Z'
  },
];

const mockZonas: Zone[] = [
  { id: 'z1', description: 'Zona Norte' },
];

const mockVendedores: Vendedor[] = [
  {
    id: 'v1',
    full_name: 'Ana Gomez',
    doi: '123456',
    email: 'ana@test.com',
    phone: '555-1111'
  },
  {
    id: 'v2',
    full_name: 'Juan Perez',
    doi: '456789',
    email: 'juan@test.com',
    phone: '555-2222'
  }
];

let mockPlanVentaService: jasmine.SpyObj<PlanVentaService>;
let mockProductService: jasmine.SpyObj<ProductService>;
let mockVendedorService: jasmine.SpyObj<VendedorService>;

describe('CrearPlanVentaComponent', () => {
  let component: CrearPlanVentaComponent;
  let fixture: ComponentFixture<CrearPlanVentaComponent>;

  beforeEach(async () => {
    mockPlanVentaService = jasmine.createSpyObj('PlanVentaService', ['createPlanVenta']);
    mockProductService = jasmine.createSpyObj('ProductService', ['getProducts']);
    mockVendedorService = jasmine.createSpyObj('VendedorService', ['getZonas', 'getVendedores']);

    await TestBed.configureTestingModule({
      imports: [
        CrearPlanVentaComponent,
        ReactiveFormsModule,
        CommonModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: PlanVentaService, useValue: mockPlanVentaService },
        { provide: ProductService, useValue: mockProductService },
        { provide: VendedorService, useValue: mockVendedorService },
      ]
    })
      .overrideComponent(CrearPlanVentaComponent, {
        set: {
          imports: [CommonModule, ReactiveFormsModule, StubLoadingSpinnerComponent]
        }
      })
      .compileComponents();

    fixture = TestBed.createComponent(CrearPlanVentaComponent);
    component = fixture.componentInstance;

    mockProductService.getProducts.and.returnValue(
      of({ products: mockProductos, total_count: 2 })
    );
    mockVendedorService.getZonas.and.returnValue(of(mockZonas));
    mockVendedorService.getVendedores.and.returnValue(
      of({ sellers: mockVendedores, total_count: 2 })
    );
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('ngOnInit and Data Loading', () => {
    it('should call cargarDatos on init and load data successfully', fakeAsync(() => {
      spyOn(console, 'error');
      fixture.detectChanges();
      flushMicrotasks();
      fixture.detectChanges();

      expect(mockProductService.getProducts).toHaveBeenCalledWith(1, 1000);
      expect(component.productos).toEqual(mockProductos);
      expect(component.zonas).toEqual(mockZonas);
      expect(component.vendedores).toEqual(mockVendedores);
      expect(component.isLoadingData).toBeFalse();
      expect(console.error).not.toHaveBeenCalled();
    }));

    it('should handle error during data loading', fakeAsync(() => {
      const errorMsg = { status: 500, message: 'Error de Red' };
      mockProductService.getProducts.and.returnValue(throwError(() => errorMsg));
      spyOn(console, 'error');

      fixture.detectChanges();
      flushMicrotasks();
      fixture.detectChanges();

      expect(component.isLoadingData).toBeFalse();
      expect(console.error).toHaveBeenCalledWith('Error al cargar datos:', errorMsg);
      expect(component.toastMessage).toBe('Error al cargar los datos necesarios');
      expect(component.toastType).toBe('error');
      flush();
    }));
  });

  describe('filtrarVendedores', () => {
    it('should reset vendedoresFiltrados if searchTerm is empty', () => {
      component.vendedores = mockVendedores;
      component.filtrarVendedores('');
      expect(component.vendedoresFiltrados).toEqual(mockVendedores);
    });

    it('should reset vendedoresFiltrados if searchTerm is spaces', () => {
      component.vendedores = mockVendedores;
      component.filtrarVendedores('   ');
      expect(component.vendedoresFiltrados).toEqual(mockVendedores);
    });

    it('should filter vendedores correctly when searchTerm is provided', () => {
      component.vendedores = mockVendedores;
      component.filtrarVendedores('Juan');
      expect(component.vendedoresFiltrados.length).toBe(1);
      expect(component.vendedoresFiltrados[0].full_name).toBe('Juan Perez');
    });

    it('should return empty array if no vendedores match searchTerm', () => {
      component.vendedores = mockVendedores;
      component.filtrarVendedores('NoExiste');
      expect(component.vendedoresFiltrados.length).toBe(0);
    });
  });

  describe('crearPlanDeVenta', () => {
    beforeEach(fakeAsync(() => {
      fixture.detectChanges();
      flushMicrotasks();
      fixture.detectChanges();
    }));

    it('should NOT submit if form is invalid', fakeAsync(() => {
      component.crearPlanDeVenta();
      expect(mockPlanVentaService.createPlanVenta).not.toHaveBeenCalled();
      expect(component.toastMessage).toBe('Por favor, complete todos los campos correctamente.');
      flush();
    }));

    it('should submit if form is valid and handle success', fakeAsync(() => {
      component.planVentaForm.setValue({
        periodo: 'Marzo',
        producto: 'p1',
        metaUnidades: 200,
        zona: 'z1',
        vendedorAsociado: 'v1',
        searchVendedor: ''
      });

      const mockResponse: CreatePlanVentaResponse = {
        id: 'plan123',
        period: 'Marzo',
        goal: 200
      };
      mockPlanVentaService.createPlanVenta.and.returnValue(of(mockResponse));
      spyOn(component.planVentaForm, 'reset').and.callThrough();

      component.crearPlanDeVenta();
      tick();
      fixture.detectChanges();

      const expectedRequest = {
        period: 'Marzo',
        goal: 200,
        product_id: 'p1',
        zone_id: 'z1',
        seller_id: 'v1'
      };
      expect(mockPlanVentaService.createPlanVenta).toHaveBeenCalledWith(expectedRequest);
      expect(component.isLoading).toBeFalse();
      expect(component.toastMessage).toBe('¡Plan de venta creado con éxito!');
      expect(component.planVentaForm.reset).toHaveBeenCalled();
      flush();
    }));

    it('should handle 409 Conflict error', fakeAsync(() => {
      component.planVentaForm.setValue({ periodo: 'M', producto: 'p', metaUnidades: 1, zona: 'z', vendedorAsociado: 'v', searchVendedor: '' });
      const errorResponse = { status: 409, error: { detail: 'Conflict' } };
      mockPlanVentaService.createPlanVenta.and.returnValue(throwError(() => errorResponse));

      component.crearPlanDeVenta();
      tick();
      fixture.detectChanges();

      expect(component.isLoading).toBeFalse();
      expect(component.toastMessage).toBe('Ya existe un plan de venta con estos mismos datos.');
      flush();
    }));

    it('should handle 422 Validation error', fakeAsync(() => {
      component.planVentaForm.setValue({ periodo: 'M', producto: 'p', metaUnidades: 1, zona: 'z', vendedorAsociado: 'v', searchVendedor: '' });
      const errorResponse = { status: 422, error: { detail: 'Invalid data' } };
      mockPlanVentaService.createPlanVenta.and.returnValue(throwError(() => errorResponse));

      component.crearPlanDeVenta();
      tick();
      expect(component.toastMessage).toBe('Uno de los datos seleccionados no es válido.');
      flush();
    }));

    it('should handle error with detail as string', fakeAsync(() => {
      component.planVentaForm.setValue({ periodo: 'M', producto: 'p', metaUnidades: 1, zona: 'z', vendedorAsociado: 'v', searchVendedor: '' });
      const errorResponse = { status: 500, error: { detail: 'Error específico del servidor.' } };
      mockPlanVentaService.createPlanVenta.and.returnValue(throwError(() => errorResponse));

      component.crearPlanDeVenta();
      tick();
      expect(component.toastMessage).toBe('Error específico del servidor.');
      flush();
    }));

    it('should handle error with detail as array', fakeAsync(() => {
      component.planVentaForm.setValue({ periodo: 'M', producto: 'p', metaUnidades: 1, zona: 'z', vendedorAsociado: 'v', searchVendedor: '' });
      const errorResponse = { status: 500, error: { detail: [{ msg: 'Mensaje de error' }] } };
      mockPlanVentaService.createPlanVenta.and.returnValue(throwError(() => errorResponse));

      component.crearPlanDeVenta();
      tick();
      expect(component.toastMessage).toBe('Mensaje de error');
      flush();
    }));

    it('should handle error with no detail', fakeAsync(() => {
      component.planVentaForm.setValue({ periodo: 'M', producto: 'p', metaUnidades: 1, zona: 'z', vendedorAsociado: 'v', searchVendedor: '' });
      const errorResponse = { status: 500, error: {} };
      mockPlanVentaService.createPlanVenta.and.returnValue(throwError(() => errorResponse));

      component.crearPlanDeVenta();
      tick();
      expect(component.toastMessage).toBe('Hubo un error al crear el plan de venta.');
      flush();
    }));
  });

  describe('Toast Message', () => {
    it('should show toast and hide it after 5 seconds', fakeAsync(() => {
      component.showToast('Test Message', 'success');
      fixture.detectChanges();

      expect(component.toastMessage).toBe('Test Message');

      tick(5000);
      fixture.detectChanges();

      expect(component.toastMessage).toBeNull();
    }));
  });
  describe('obtenerMensajeDeError utility', () => {


    it('should return specific messages for known status codes', () => {
      const err409 = { status: 409, error: {} };
      const err422 = { status: 422, error: {} };
      expect((component as any).obtenerMensajeDeError(err409)).toBe(
        'Ya existe un plan de venta con estos mismos datos.'
      );
      expect((component as any).obtenerMensajeDeError(err422)).toBe(
        'Uno de los datos seleccionados no es válido.'
      );
    });

    it('should return detail string if provided', () => {
      const err = { status: 500, error: { detail: 'Error específico del servidor.' } };
      expect((component as any).obtenerMensajeDeError(err)).toBe('Error específico del servidor.');
    });

    it('should return first msg from detail array if provided', () => {
      const err = { status: 500, error: { detail: [{ msg: 'Mensaje de error' }] } };
      expect((component as any).obtenerMensajeDeError(err)).toBe('Mensaje de error');
    });

    it('should return generic message if no detail is provided', () => {
      const err = { status: 500, error: {} };
      expect((component as any).obtenerMensajeDeError(err)).toBe('Hubo un error al crear el plan de venta.');
    });

    it('should return generic message if err is null', () => {
      expect((component as any).obtenerMensajeDeError(null)).toBe(
        'Hubo un error al crear el plan de venta.'
      );
    });

    it('should return generic message if err is undefined', () => {
      expect((component as any).obtenerMensajeDeError(undefined)).toBe(
        'Hubo un error al crear el plan de venta.'
      );
    });

  });

  describe('Full branch coverage', () => {

    it('should build request correctly using construirRequest', () => {
      const formValue = {
        periodo: 'Abril',
        producto: 'p1',
        metaUnidades: 50,
        zona: 'z1',
        vendedorAsociado: 'v1'
      };
      const result = (component as any).construirRequest(formValue);
      expect(result).toEqual({
        period: 'Abril',
        goal: 50,
        product_id: 'p1',
        zone_id: 'z1',
        seller_id: 'v1'
      });
    });

    it('should execute procesarExito directly', () => {
      component.vendedores = mockVendedores;
      (component as any).procesarExito();
      expect(component.toastMessage).toBe('¡Plan de venta creado con éxito!');
      expect(component.vendedoresFiltrados).toEqual(mockVendedores);
    });

    it('should execute finalizarCarga directly', () => {
      component.isLoading = true;
      (component as any).finalizarCarga();
      expect(component.isLoading).toBeFalse();
    });

    it('should execute procesarError with null', () => {
      (component as any).procesarError(null);
      expect(component.toastMessage).toBe('Hubo un error al crear el plan de venta.');
    });

    it('should execute procesarError with undefined', () => {
      (component as any).procesarError(undefined);
      expect(component.toastMessage).toBe('Hubo un error al crear el plan de venta.');
    });

    it('should handle searchVendedor valueChanges correctly', fakeAsync(() => {
      component.vendedores = mockVendedores;
      component.vendedoresFiltrados = mockVendedores;

      fixture.detectChanges();
      tick();

      component.searchVendedor?.setValue('Ana');
      tick();
      expect(component.vendedoresFiltrados.length).toBe(1);
      expect(component.vendedoresFiltrados[0].full_name).toBe('Ana Gomez');

      component.searchVendedor?.setValue('Juan');
      tick();
      expect(component.vendedoresFiltrados.length).toBe(1);
      expect(component.vendedoresFiltrados[0].full_name).toBe('Juan Perez');

      component.searchVendedor?.setValue('NoExiste');
      tick();
      expect(component.vendedoresFiltrados.length).toBe(0);

      component.searchVendedor?.setValue('   ');
      tick();
      expect(component.vendedoresFiltrados).toEqual(mockVendedores);
    }));


    it('should handle showToast multiple calls correctly', fakeAsync(() => {
      component.showToast('Mensaje 1', 'success');
      component.showToast('Mensaje 2', 'error');
      expect(component.toastMessage).toBe('Mensaje 2');
      expect(component.toastType).toBe('error');

      tick(5000);
      expect(component.toastMessage).toBeNull();
    }));

    it('should handle obtenerMensajeDeError with all edge cases', () => {
      const componentAny = component as any;

      expect(componentAny.obtenerMensajeDeError(null)).toBe('Hubo un error al crear el plan de venta.');
      expect(componentAny.obtenerMensajeDeError(undefined)).toBe('Hubo un error al crear el plan de venta.');

      expect(componentAny.obtenerMensajeDeError({ status: 409, error: {} })).toBe(
        'Ya existe un plan de venta con estos mismos datos.'
      );
      expect(componentAny.obtenerMensajeDeError({ status: 422, error: {} })).toBe(
        'Uno de los datos seleccionados no es válido.'
      );

      expect(componentAny.obtenerMensajeDeError({ status: 500, error: { detail: 'Error string' } })).toBe(
        'Error string'
      );
      expect(componentAny.obtenerMensajeDeError({ status: 500, error: { detail: [{ msg: 'Error array' }] } })).toBe(
        'Error array'
      );
      expect(componentAny.obtenerMensajeDeError({ status: 500, error: { detail: [] } })).toBe(
        'Error desconocido.'
      );
      expect(componentAny.obtenerMensajeDeError({ status: 500, error: {} })).toBe(
        'Hubo un error al crear el plan de venta.'
      );
    });

  });


});
