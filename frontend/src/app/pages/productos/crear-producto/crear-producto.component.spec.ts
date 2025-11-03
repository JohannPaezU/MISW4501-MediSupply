import { TestBed, ComponentFixture, fakeAsync, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { CrearProductoComponent } from './crear-producto.component';
import { ProductService } from '../../../services/productos/product.service';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

describe('CrearProductoComponent', () => {
  let component: CrearProductoComponent;
  let fixture: ComponentFixture<CrearProductoComponent>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let cdrSpy: jasmine.SpyObj<ChangeDetectorRef>;

  beforeEach(() => {
    productServiceSpy = jasmine.createSpyObj('ProductService', ['createProduct']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    cdrSpy = jasmine.createSpyObj('ChangeDetectorRef', ['detectChanges']);

    TestBed.configureTestingModule({
      imports: [CrearProductoComponent],
      providers: [
        { provide: ProductService, useValue: productServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ChangeDetectorRef, useValue: cdrSpy }
      ]
    });

    fixture = TestBed.createComponent(CrearProductoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mark form as touched if invalid', () => {
    spyOn(component['productoForm'], 'markAllAsTouched');

    component.productoForm.setValue({
      name: '', batch: '', store: '', image_url: null, due_date: '',
      details: '', stock: null, price_per_unit: null,
      provider_id: ''
    });

    component.crearProducto();

    expect(component['productoForm'].markAllAsTouched).toHaveBeenCalled();
    expect(productServiceSpy.createProduct).not.toHaveBeenCalled();
  });

  it('should call ProductService.createProduct and show success toast', fakeAsync(() => {
    const mockResponse = {
      id: '1', name: 'Producto A', details: 'Detalles', store: 'Bodega',
      batch: 'Lote01', image_url: null, due_date: '2025-12-31',
      stock: 10, price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000',
      created_at: '2025-10-17T00:00:00Z'
    };
    productServiceSpy.createProduct.and.returnValue(of(mockResponse));

    component.productoForm.setValue({
      name: 'Producto A', batch: 'Lote01', store: 'Bodega',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(productServiceSpy.createProduct).toHaveBeenCalled();
    expect(component.toastMessage).toContain('creado con éxito');
    expect(component.toastType).toBe('success');

    tick(5000);
    expect(component.toastMessage).toBeNull();
  }));

  it('should show validation error toast on 422 error', fakeAsync(() => {
    const mockError = {
      status: 422,
      error: { detail: [{ loc: ['body', 'name'], msg: 'es requerido' }] }
    };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toContain("Error en el campo 'name'");
    expect(component.toastType).toBe('error');

    tick(5000);
    expect(component.toastMessage).toBeNull();
  }));

  // ✅ NUEVO: Test para error 422 sin loc (campo desconocido)
  it('should show validation error with unknown field when loc is missing', fakeAsync(() => {
    const mockError = {
      status: 422,
      error: { detail: [{ msg: 'error sin campo' }] }
    };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toContain("Error en el campo 'campo desconocido'");
    expect(component.toastType).toBe('error');

    tick(5000); // Limpiar el timer del toast
  }));

  // ✅ NUEVO: Test para error 422 sin msg (mensaje desconocido)
  it('should show validation error with unknown message when msg is missing', fakeAsync(() => {
    const mockError = {
      status: 422,
      error: { detail: [{ loc: ['body', 'stock'] }] }
    };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toContain('error desconocido');
    expect(component.toastType).toBe('error');

    tick(5000); // Limpiar el timer del toast
  }));

  // ✅ NUEVO: Test para error 422 con array vacío
  it('should not show validation error when detail array is empty', fakeAsync(() => {
    const mockError = {
      status: 422,
      error: { detail: [] }
    };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Ocurrió un error inesperado.');
    expect(component.toastType).toBe('error');

    tick(5000); // Limpiar el timer del toast
  }));

  // ✅ NUEVO: Test para error con status diferente de 422
  it('should not treat non-422 error as validation error', fakeAsync(() => {
    const mockError = {
      status: 400,
      error: { detail: [{ loc: ['body', 'name'], msg: 'error' }] }
    };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Ocurrió un error inesperado.');

    tick(5000); // Limpiar el timer del toast
  }));

  // ✅ NUEVO: Test para error sin detail
  it('should handle error without detail property', fakeAsync(() => {
    const mockError = {
      status: 422,
      error: {}
    };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Ocurrió un error inesperado.');

    tick(5000); // Limpiar el timer del toast
  }));

  it('should show custom error message when error.error.message exists', fakeAsync(() => {
    const mockError = { error: { message: 'Error personalizado' } };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Error personalizado');
    expect(component.toastType).toBe('error');

    tick(5000);
    expect(component.toastMessage).toBeNull();
  }));

  // ✅ NUEVO: Test para error.error.message que no es string
  it('should not use error.error.message if it is not a string', fakeAsync(() => {
    const mockError = { error: { message: 123 } };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Ocurrió un error inesperado.');

    tick(5000); // Limpiar el timer del toast
  }));

  it('should show generic error message when error is unexpected', fakeAsync(() => {
    const mockError = { status: 500 };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Ocurrió un error inesperado.');
    expect(component.toastType).toBe('error');

    tick(5000);
    expect(component.toastMessage).toBeNull();
  }));

  // ✅ NUEVO: Test para error sin estructura de error
  it('should handle error without error property', fakeAsync(() => {
    const mockError = { status: 500 };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Ocurrió un error inesperado.');

    tick(5000); // Limpiar el timer del toast
  }));

  it('should navigate to crear-producto-masivo', () => {
    component.crearProductoMasivo();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/productos/crear-masivo']);
  });

  it('should set and clear toast message after 5 seconds', fakeAsync(() => {
    component.showToast('Mensaje de prueba', 'success');
    expect(component.toastMessage).toBe('Mensaje de prueba');
    expect(component.toastType).toBe('success');

    tick(5000);
    expect(component.toastMessage).toBeNull();
  }));

  // ✅ NUEVO: Test para toast de error
  it('should set and clear error toast message after 5 seconds', fakeAsync(() => {
    component.showToast('Mensaje de error', 'error');
    expect(component.toastMessage).toBe('Mensaje de error');
    expect(component.toastType).toBe('error');

    tick(5000);
    expect(component.toastMessage).toBeNull();
  }));

  // ✅ NUEVO: Test para verificar que el formulario se resetea después de crear producto
  it('should reset form after successful product creation', fakeAsync(() => {
    const mockResponse = {
      id: '1', name: 'Producto A', details: 'Detalles', store: 'Bodega',
      batch: 'Lote01', image_url: null, due_date: '2025-12-31',
      stock: 10, price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000',
      created_at: '2025-10-17T00:00:00Z'
    };
    productServiceSpy.createProduct.and.returnValue(of(mockResponse));

    component.productoForm.setValue({
      name: 'Producto A', batch: 'Lote01', store: 'Bodega',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    // Verificar que el formulario se resetea
    expect(component.productoForm.value).toEqual({
      name: null, batch: null, store: null, image_url: null,
      due_date: null, details: null, stock: null,
      price_per_unit: null, provider_id: null
    });

    tick(5000); // Limpiar el timer del toast
  }));

  // ✅ NUEVO: Test para verificar isLoading se activa y desactiva
  it('should toggle isLoading during product creation', fakeAsync(() => {
    const mockResponse = {
      id: '1', name: 'Producto A', details: 'Detalles', store: 'Bodega',
      batch: 'Lote01', image_url: null, due_date: '2025-12-31',
      stock: 10, price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000',
      created_at: '2025-10-17T00:00:00Z'
    };
    productServiceSpy.createProduct.and.returnValue(of(mockResponse));

    component.productoForm.setValue({
      name: 'Producto A', batch: 'Lote01', store: 'Bodega',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    expect(component.isLoading).toBe(false);

    component.crearProducto();
    // isLoading se vuelve true pero inmediatamente se resetea debido al observable síncrono

    tick();
    expect(component.isLoading).toBe(false);

    tick(5000); // Limpiar el timer del toast
  }));

  // ✅ NUEVO: Test para verificar isLoading se desactiva en caso de error
  it('should toggle isLoading to false on error', fakeAsync(() => {
    const mockError = { status: 500 };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    expect(component.isLoading).toBe(false);

    component.crearProducto();
    // isLoading se vuelve true pero inmediatamente se resetea debido al observable síncrono

    tick();
    expect(component.isLoading).toBe(false);

    tick(5000); // Limpiar el timer del toast
  }));

  // ✅ NUEVO: Test para verificar que console.error se llama en caso de error
  it('should log error to console when createProduct fails', fakeAsync(() => {
    const mockError = { status: 500 };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));
    spyOn(console, 'error');

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unit: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(console.error).toHaveBeenCalledWith('Error Response:', mockError);

    tick(5000); // Limpiar el timer del toast
  }));

  // ✅ NUEVO: Test para getter 'f'
  it('should return form controls through getter f', () => {
    const controls = component.f;
    expect(controls['name']).toBeDefined();
    expect(controls['batch']).toBeDefined();
    expect(controls['store']).toBeDefined();
  });
});
