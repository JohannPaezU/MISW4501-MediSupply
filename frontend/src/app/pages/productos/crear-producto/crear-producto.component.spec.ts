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
      details: '', stock: null, price_per_unite: null,
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
      stock: 10, price_per_unite: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000',
      created_at: '2025-10-17T00:00:00Z'
    };
    productServiceSpy.createProduct.and.returnValue(of(mockResponse));

    component.productoForm.setValue({
      name: 'Producto A', batch: 'Lote01', store: 'Bodega',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles del producto', stock: 10,
      price_per_unite: 5.5,
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
      price_per_unite: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toContain("Error en el campo 'name'");
    expect(component.toastType).toBe('error');

    tick(5000);
    expect(component.toastMessage).toBeNull();
  }));

  it('should show custom error message when error.error.message exists', fakeAsync(() => {
    const mockError = { error: { message: 'Error personalizado' } };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unite: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Error personalizado');
    expect(component.toastType).toBe('error');

    tick(5000);
    expect(component.toastMessage).toBeNull();
  }));

  it('should show generic error message when error is unexpected', fakeAsync(() => {
    const mockError = { status: 500 };
    productServiceSpy.createProduct.and.returnValue(throwError(() => mockError));

    component.productoForm.setValue({
      name: 'Producto Test', batch: 'Lote123', store: 'Bodega Central',
      image_url: null, due_date: '2025-12-31',
      details: 'Detalles completos del producto', stock: 10,
      price_per_unite: 5.5,
      provider_id: '123e4567-e89b-12d3-a456-426614174000'
    });

    component.crearProducto();
    tick();

    expect(component.toastMessage).toBe('Ocurrió un error inesperado.');
    expect(component.toastType).toBe('error');

    tick(5000);
    expect(component.toastMessage).toBeNull();
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
});
