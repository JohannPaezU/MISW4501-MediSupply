import { ComponentFixture, TestBed, fakeAsync, flush, tick, discardPeriodicTasks } from '@angular/core/testing';
import { CrearProveedorComponent } from './crear-proveedor.component';
import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('CrearProveedorComponent', () => {
  let component: CrearProveedorComponent;
  let fixture: ComponentFixture<CrearProveedorComponent>;
  let proveedorServiceSpy: jasmine.SpyObj<ProveedorService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const proveedorServiceMock = jasmine.createSpyObj('ProveedorService', ['createProvider']);
    const routerMock = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, CrearProveedorComponent],
      providers: [
        { provide: ProveedorService, useValue: proveedorServiceMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CrearProveedorComponent);
    component = fixture.componentInstance;
    proveedorServiceSpy = TestBed.inject(ProveedorService) as jasmine.SpyObj<ProveedorService>;
    routerSpy = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture.detectChanges();
  });

  function fillForm() {
    component.proveedorForm.setValue({
      name: 'Proveedor X',
      rit: '123456',
      city: 'Medellín',
      country: 'Colombia',
      email: 'test@proveedor.com',
      phone: '3001234567',
      image_url: ''
    });
  }

  it('debería crearse el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería marcar los campos como tocados si el formulario es inválido', () => {
    spyOn(component.proveedorForm, 'markAllAsTouched');
    component.crearProveedor();
    expect(component.proveedorForm.markAllAsTouched).toHaveBeenCalled();
  });

  it('debería llamar al servicio y mostrar mensaje de éxito al crear proveedor', fakeAsync(() => {
    fillForm();
    const mockResponse = { name: 'Proveedor X' } as any;
    proveedorServiceSpy.createProvider.and.returnValue(of(mockResponse));

    const cdrSpy = spyOn(component['cdr'], 'detectChanges');

    component.crearProveedor();
    tick();

    expect(proveedorServiceSpy.createProvider).toHaveBeenCalledWith(jasmine.objectContaining({
      name: 'Proveedor X',
      email: 'test@proveedor.com'
    }));

    expect(component.successMessage).toContain('Proveedor "Proveedor X" creado con éxito');
    expect(component.toastMessage).toBe('Proveedor creado exitosamente');
    expect(component.toastType).toBe('success');
    expect(cdrSpy).toHaveBeenCalled();

    tick(3000);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/proveedores/lista-proveedores']);

    tick(2000);
    discardPeriodicTasks();
    flush();
  }));

  it('debería manejar error 409 (conflicto) correctamente', fakeAsync(() => {
    fillForm();
    const error = { status: 409 };
    proveedorServiceSpy.createProvider.and.returnValue(throwError(() => error));

    component.crearProveedor();
    tick();

    expect(component.errorMessage).toBe('Ya existe un proveedor con este correo electrónico o RIT.');
    expect(component.toastType).toBe('error');

    tick(3000);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/proveedores/lista-proveedores']);

    tick(2000);
    flush();
  }));

  it('debería manejar error 422 (validación) correctamente', fakeAsync(() => {
    fillForm();
    const error = {
      status: 422,
      error: {
        detail: [
          { loc: ['body', 'email'], msg: 'email inválido' }
        ]
      }
    };
    proveedorServiceSpy.createProvider.and.returnValue(throwError(() => error));

    component.crearProveedor();
    tick();

    expect(component.errorMessage).toBe(`Error en el campo 'email': email inválido`);
    expect(component.toastType).toBe('error');

    tick(3000);
    tick(2000);
    flush();
  }));

  it('debería manejar error 422 sin campo loc definido', fakeAsync(() => {
    fillForm();
    const error = {
      status: 422,
      error: {
        detail: [
          { msg: 'error sin campo específico' }
        ]
      }
    };
    proveedorServiceSpy.createProvider.and.returnValue(throwError(() => error));

    component.crearProveedor();
    tick();

    expect(component.errorMessage).toBe(`Error en el campo 'desconocido': error sin campo específico`);
    expect(component.toastType).toBe('error');

    tick(3000);
    tick(2000);
    flush();
  }));

  it('debería manejar error 422 sin mensaje definido', fakeAsync(() => {
    fillForm();
    const error = {
      status: 422,
      error: {
        detail: [
          { loc: ['body', 'phone'] }
        ]
      }
    };
    proveedorServiceSpy.createProvider.and.returnValue(throwError(() => error));

    component.crearProveedor();
    tick();

    expect(component.errorMessage).toBe(`Error en el campo 'phone': Error desconocido.`);
    expect(component.toastType).toBe('error');

    tick(3000);
    tick(2000);
    flush();
  }));

  it('debería manejar error genérico correctamente', fakeAsync(() => {
    fillForm();
    const error = { status: 500 };
    proveedorServiceSpy.createProvider.and.returnValue(throwError(() => error));

    component.crearProveedor();
    tick();

    expect(component.errorMessage).toBe('Ocurrió un error inesperado al crear el proveedor.');
    expect(component.toastType).toBe('error');

    tick(3000);
    tick(2000);
    flush();
  }));

  it('debería limpiar el toast después de 5 segundos', fakeAsync(() => {
    component.showToast('Test mensaje', 'success');
    expect(component.toastMessage).toBe('Test mensaje');
    expect(component.toastType).toBe('success');

    tick(5000);
    expect(component.toastMessage).toBeNull();
    expect(component.toastType).toBe('success');
    flush();
  }));
});
