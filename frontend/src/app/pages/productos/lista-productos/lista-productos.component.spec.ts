import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ListaProductosComponent } from './lista-productos.component';
import { ProductService } from '../../../services/productos/product.service';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { CsvExportService } from '../../../services/utilities/csv.service';

describe('ListaProductosComponent', () => {
  let component: ListaProductosComponent;
  let fixture: ComponentFixture<ListaProductosComponent>;
  let productServiceSpy: jasmine.SpyObj<ProductService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let cdrSpy: jasmine.SpyObj<ChangeDetectorRef>;
  let csvExportSpy: jasmine.SpyObj<CsvExportService>;

  beforeEach(() => {
    productServiceSpy = jasmine.createSpyObj('ProductService', ['getProducts']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    cdrSpy = jasmine.createSpyObj('ChangeDetectorRef', ['detectChanges']);
    csvExportSpy = jasmine.createSpyObj('CsvExportService', ['exportarACsv']);

    TestBed.configureTestingModule({
      imports: [ListaProductosComponent],
      providers: [
        { provide: ProductService, useValue: productServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ChangeDetectorRef, useValue: cdrSpy },
        { provide: CsvExportService, useValue: csvExportSpy }
      ]
    });

    fixture = TestBed.createComponent(ListaProductosComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load products on init', fakeAsync(() => {
    const mockData = {
      total_count: 2,
      products: [
        { id: '1', name: 'A', details: '', store: '', batch: '', image_url: null, due_date: '', stock: 10, price_per_unite: 5, provider_id: '', created_at: '' },
        { id: '2', name: 'B', details: '', store: '', batch: '', image_url: null, due_date: '', stock: 5, price_per_unite: 3, provider_id: '', created_at: '' }
      ]
    };
    productServiceSpy.getProducts.and.returnValue(of(mockData));

    component.ngOnInit();
    tick();

    expect(component.productos.length).toEqual(2);
    expect(component.totalItems).toEqual(2);
    expect(component.isLoading).toBeFalse();
    expect(component.hasError).toBeFalse();
  }));

  it('should handle error when loading products', fakeAsync(() => {
    productServiceSpy.getProducts.and.returnValue(throwError(() => ({ status: 500 })));

    component.cargarProductos();
    tick();

    expect(component.hasError).toBeTrue();
    expect(component.isLoading).toBeFalse();
    expect(component.toastMessage).toEqual('Error al cargar los productos.');
    expect(component.toastType).toEqual('error');

    tick(4000);
    expect(component.toastMessage).toBeNull();
  }));

  it('should go to next page', () => {
    component.currentPage = 1;
    component.pageSize = 10;
    component.totalItems = 50;
    spyOn(component, 'cargarProductos');

    component.nextPage();

    expect(component.currentPage).toEqual(2);
    expect(component.cargarProductos).toHaveBeenCalled();
  });

  it('should not go to next page if last page', () => {
    component.currentPage = 5;
    component.pageSize = 10;
    component.totalItems = 50;
    spyOn(component, 'cargarProductos');

    component.nextPage();

    expect(component.currentPage).toEqual(5);
    expect(component.cargarProductos).not.toHaveBeenCalled();
  });

  it('should go to previous page', () => {
    component.currentPage = 3;
    spyOn(component, 'cargarProductos');

    component.previousPage();

    expect(component.currentPage).toEqual(2);
    expect(component.cargarProductos).toHaveBeenCalled();
  });

  it('should not go to previous page if first page', () => {
    component.currentPage = 1;
    spyOn(component, 'cargarProductos');

    component.previousPage();

    expect(component.currentPage).toEqual(1);
    expect(component.cargarProductos).not.toHaveBeenCalled();
  });

  it('should reset to page 1 when page size changes', () => {
    component.currentPage = 3;
    component.pageSize = 10;
    spyOn(component, 'cargarProductos');

    const event = { target: { value: '20' } } as unknown as Event;
    component.onPageSizeChange(event);

    expect(component.pageSize).toEqual(20);
    expect(component.currentPage).toEqual(1);
    expect(component.cargarProductos).toHaveBeenCalled();
  });

  it('should navigate to crear producto', () => {
    component.crearProducto();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/productos/crear']);
  });

  it('should navigate to product detail', () => {
    const product = { id: 'abc' } as any;
    component.verDetalle(product);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/productos/detalle', 'abc']);
  });

  it('should export CSV if products exist', fakeAsync(() => {
    component.productos = [
      { id: '1', name: 'A', details: '', store: '', batch: '', image_url: null, due_date: '', stock: 5, price_per_unite: 3, provider_id: '', created_at: '' }
    ];

    component.exportarCSV();
    tick();

    expect(csvExportSpy.exportarACsv).toHaveBeenCalled();
    expect(component.toastMessage).toEqual('Productos exportados correctamente.');
    expect(component.toastType).toEqual('success');

    tick(4000);
    expect(component.toastMessage).toBeNull();
  }));

  it('should show error toast if no products for CSV', () => {
    component.productos = [];
    component.exportarCSV();

    expect(csvExportSpy.exportarACsv).not.toHaveBeenCalled();
    expect(component.toastMessage).toEqual('No hay productos para exportar.');
    expect(component.toastType).toEqual('error');
  });


  it('should show and clear toast', fakeAsync(() => {
    component.showToast('Mensaje prueba', 'success');
    expect(component.toastMessage).toEqual('Mensaje prueba');
    expect(component.toastType).toEqual('success');

    tick(4000);
    expect(component.toastMessage).toBeNull();
  }));
});
