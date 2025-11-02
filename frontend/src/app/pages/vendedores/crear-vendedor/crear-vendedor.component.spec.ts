import { ComponentFixture, TestBed, fakeAsync, tick, flush } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { of, throwError } from 'rxjs';

import { CrearVendedorComponent } from './crear-vendedor.component';
import { VendedorService } from '../../../services/vendedores/vendedor.service';
import { Zone, CreateVendedorResponse } from '../../../interfaces/vendedor.interface';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  template: ''
})
class StubLoadingSpinnerComponent { }

const mockZonas: Zone[] = [
  { id: 'z1', description: 'Zona Norte' },
  { id: 'z2', description: 'Zona Sur' },
];

const mockVendedorCreado: CreateVendedorResponse = {
  id: 'v123',
  full_name: 'Ana García',
  doi: '12345678',
  email: 'ana.garcia@test.com',
  phone: '987654321',
  created_at: '2023-01-01T00:00:00Z'
};

let mockVendedorService: jasmine.SpyObj<VendedorService>;

describe('CrearVendedorComponent', () => {
  let component: CrearVendedorComponent;
  let fixture: ComponentFixture<CrearVendedorComponent>;

  beforeEach(async () => {
    mockVendedorService = jasmine.createSpyObj('VendedorService', ['getZonas', 'createVendedor']);

    await TestBed.configureTestingModule({
      imports: [
        CrearVendedorComponent,
        ReactiveFormsModule,
        CommonModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: VendedorService, useValue: mockVendedorService },
      ]
    })
      .overrideComponent(CrearVendedorComponent, {
        set: {
          imports: [CommonModule, ReactiveFormsModule, StubLoadingSpinnerComponent]
        }
      })
      .compileComponents();

    mockVendedorService.getZonas.and.returnValue(of(mockZonas));
    mockVendedorService.createVendedor.and.returnValue(of(mockVendedorCreado));

    fixture = TestBed.createComponent(CrearVendedorComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Initialization and Zone Loading', () => {
    it('should load zones successfully on init', () => {
      expect(component.isLoadingZones).toBeTrue();

      fixture.detectChanges();

      expect(mockVendedorService.getZonas).toHaveBeenCalled();
      expect(component.isLoadingZones).toBeFalse();
      expect(component.zonas.length).toBe(2);
      expect(component.zonas[0].description).toBe('Zona Norte');
    });

    it('should handle error when loading zones', fakeAsync(() => {
      mockVendedorService.getZonas.and.returnValue(throwError(() => new Error('Error de red')));
      spyOn(console, 'error');

      fixture.detectChanges();

      expect(mockVendedorService.getZonas).toHaveBeenCalled();
      expect(component.isLoadingZones).toBeFalse();
      expect(component.zonas.length).toBe(0);
      expect(console.error).toHaveBeenCalledWith('Error al cargar las zonas:', jasmine.any(Error));
      expect(component.toastMessage).toBe('Error al cargar las zonas. Intente de nuevo.');

      tick(5000); // Limpiar timer del toast
    }));
  });

  describe('Form Validation', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should be invalid when empty', () => {
      expect(component.vendedorForm.valid).toBeFalse();
    });

    it('should validate nombreCompleto (required, min, max)', () => {
      const control = component.nombreCompleto;
      expect(control?.valid).toBeFalse();

      control?.setValue('a'.repeat(101));
      expect(control?.hasError('maxlength')).toBeTrue();

      control?.setValue('Juan');
      expect(control?.valid).toBeTrue();
    });

    // ✅ NUEVO: Test para minlength de nombreCompleto
    it('should validate nombreCompleto minlength', () => {
      const control = component.nombreCompleto;
      control?.setValue('');
      expect(control?.hasError('required')).toBeTrue();

      control?.setValue('J');
      expect(control?.valid).toBeTrue();
    });

    // ✅ NUEVO: Test para documento minlength y maxlength
    it('should validate documento (required, min, max)', () => {
      const control = component.documento;
      expect(control?.hasError('required')).toBeTrue();

      control?.setValue('');
      expect(control?.hasError('required')).toBeTrue();

      control?.setValue('a'.repeat(51));
      expect(control?.hasError('maxlength')).toBeTrue();

      control?.setValue('12345678');
      expect(control?.valid).toBeTrue();
    });

    it('should validate correoElectronico (required, email)', () => {
      const control = component.correoElectronico;
      expect(control?.hasError('required')).toBeTrue();

      control?.setValue('invalid-email');
      expect(control?.hasError('email')).toBeTrue();

      control?.setValue('valid@email.com');
      expect(control?.valid).toBeTrue();
    });



    it('should validate telefono (required, pattern)', () => {
      const control = component.telefono;
      expect(control?.hasError('required')).toBeTrue();

      control?.setValue('123');
      expect(control?.hasError('pattern')).toBeTrue();

      control?.setValue('1234567890123456');
      expect(control?.hasError('pattern')).toBeTrue();

      control?.setValue('abcdefghi');
      expect(control?.hasError('pattern')).toBeTrue();

      control?.setValue('987654321');
      expect(control?.valid).toBeTrue();

      control?.setValue('123456789012345');
      expect(control?.valid).toBeTrue();
    });

    it('should validate zonaAsignada (required)', () => {
      const control = component.zonaAsignada;
      expect(control?.hasError('required')).toBeTrue();

      control?.setValue('z1');
      expect(control?.valid).toBeTrue();
    });

    it('should be valid when all fields are correct', () => {
      component.vendedorForm.setValue({
        nombreCompleto: 'Ana García',
        documento: '12345678',
        correoElectronico: 'ana.garcia@test.com',
        telefono: '987654321',
        zonaAsignada: 'z1'
      });
      expect(component.vendedorForm.valid).toBeTrue();
    });
  });

  describe('Form Submission (crearVendedor)', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should NOT submit if form is invalid', fakeAsync(() => {
      component.crearVendedor();

      expect(mockVendedorService.createVendedor).not.toHaveBeenCalled();
      expect(component.toastMessage).toBe('Por favor, complete todos los campos correctamente.');

      tick(5000);
    }));

    it('should submit valid form and handle success (with trimming)', fakeAsync(() => {
      component.vendedorForm.setValue({
        nombreCompleto: ' Ana García ',
        documento: ' 12345678 ',
        correoElectronico: 'ana.garcia@test.com',
        telefono: '987654321',
        zonaAsignada: 'z1'
      });

      spyOn(component.vendedorForm, 'reset').and.callThrough();

      component.crearVendedor();
      tick();

      const expectedRequest = {
        full_name: 'Ana García',
        doi: '12345678',
        email: 'ana.garcia@test.com',
        phone: '987654321',
        zone_id: 'z1'
      };
      expect(mockVendedorService.createVendedor).toHaveBeenCalledWith(expectedRequest);

      expect(component.isLoading).toBeFalse();
      expect(component.toastMessage).toBe('¡Vendedor Ana García creado con éxito!');
      expect(component.vendedorForm.reset).toHaveBeenCalled();
      expect(component.zonaAsignada?.value).toBeNull();

      tick(5000);
    }));

    it('should handle 409 Conflict error', fakeAsync(() => {
      component.vendedorForm.setValue({
        nombreCompleto: 'Ana García', documento: '12345678',
        correoElectronico: 'ana.garcia@test.com', telefono: '987654321',
        zonaAsignada: 'z1'
      });
      const error = { status: 409, error: { detail: 'Duplicate entry' } };
      mockVendedorService.createVendedor.and.returnValue(throwError(() => error));

      component.crearVendedor();
      tick();

      expect(component.isLoading).toBeFalse();
      expect(component.toastMessage).toBe('Ya existe un vendedor con este correo o documento.');

      tick(5000);
    }));

    it('should handle 422 Unprocessable Entity error', fakeAsync(() => {
      component.vendedorForm.setValue({
        nombreCompleto: 'Ana García', documento: '12345678',
        correoElectronico: 'ana.garcia@test.com', telefono: '987654321',
        zonaAsignada: 'z_invalid'
      });
      const error = { status: 422 };
      mockVendedorService.createVendedor.and.returnValue(throwError(() => error));

      component.crearVendedor();
      tick();

      expect(component.toastMessage).toBe('La zona seleccionada no existe.');

      tick(5000);
    }));

    it('should handle generic error with string detail', fakeAsync(() => {
      component.vendedorForm.setValue({
        nombreCompleto: 'Ana García', documento: '12345678',
        correoElectronico: 'ana.garcia@test.com', telefono: '987654321',
        zonaAsignada: 'z1'
      });
      const error = { status: 500, error: { detail: 'Error de base de datos.' } };
      mockVendedorService.createVendedor.and.returnValue(throwError(() => error));

      component.crearVendedor();
      tick();

      expect(component.toastMessage).toBe('Error de base de datos.');

      tick(5000);
    }));

    it('should log error to console when createVendedor fails', fakeAsync(() => {
      component.vendedorForm.setValue({
        nombreCompleto: 'Ana García', documento: '12345678',
        correoElectronico: 'ana.garcia@test.com', telefono: '987654321',
        zonaAsignada: 'z1'
      });
      const error = { status: 500 };
      mockVendedorService.createVendedor.and.returnValue(throwError(() => error));
      spyOn(console, 'error');

      component.crearVendedor();
      tick();

      expect(console.error).toHaveBeenCalledWith('Error al crear el vendedor:', error);

      tick(5000);
    }));
  });

  describe('Other Methods', () => {
    it('should show toast for importarDesdeCSV', fakeAsync(() => {
      component.importarDesdeCSV();
      expect(component.toastMessage).toBe('Funcionalidad próximamente disponible');
      expect(component.toastType).toBe('error');

      tick(5000);
    }));

    it('should show and hide success toast', fakeAsync(() => {
      component.showToast('Success message', 'success');

      expect(component.toastMessage).toBe('Success message');
      expect(component.toastType).toBe('success');

      tick(5000);
      expect(component.toastMessage).toBeNull();
    }));
  });

  describe('handleError', () => {
    const genericMessage = 'Hubo un error al crear el vendedor. Por favor, intenta de nuevo.';

    it('should return generic message if error.error.detail is undefined', () => {
      const error = { error: {} };
      const result = component['handleError'](error);
      expect(result).toBe(genericMessage);
    });

    it('should return first message if error.error.detail is array', () => {
      const error = { error: { detail: [{ msg: 'Detalle de error' }] } };
      const result = component['handleError'](error);
      expect(result).toBe('Detalle de error');
    });

    it('should return "Error desconocido." if array item has no msg', () => {
      const error = { error: { detail: [{}] } };
      const result = component['handleError'](error);
      expect(result).toBe('Error desconocido.');
    });

    it('should return detail if error.error.detail is string', () => {
      const error = { error: { detail: 'Error como string' } };
      const result = component['handleError'](error);
      expect(result).toBe('Error como string');
    });

    it('should return generic message if error.error.detail is other type', () => {
      const error = { error: { detail: 12345 } };
      const result = component['handleError'](error);
      expect(result).toBe(genericMessage);
    });

    it('should return detail string for unknown status codes', () => {
      const error = { status: 500, error: { detail: 'Error del servidor' } };
      const result = component['handleError'](error);
      expect(result).toBe('Error del servidor');
    });

    it('should return generic message if error.error is undefined', () => {
      const error = { status: 500 };
      const result = component['handleError'](error);
      expect(result).toBe(genericMessage);
    });

    it('should return duplicate message for status 409', () => {
      const error = { status: 409 };
      const result = component['handleError'](error);
      expect(result).toBe('Ya existe un vendedor con este correo o documento.');
    });

    it('should return zone error message for status 422', () => {
      const error = { status: 422 };
      const result = component['handleError'](error);
      expect(result).toBe('La zona seleccionada no existe.');
    });
  });

  describe('Getters', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should return correct form controls', () => {
      expect(component.nombreCompleto).toBe(component.vendedorForm.get('nombreCompleto'));
      expect(component.documento).toBe(component.vendedorForm.get('documento'));
      expect(component.correoElectronico).toBe(component.vendedorForm.get('correoElectronico'));
      expect(component.telefono).toBe(component.vendedorForm.get('telefono'));
      expect(component.zonaAsignada).toBe(component.vendedorForm.get('zonaAsignada'));
    });
  });

  describe('buildRequest', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should build request with trimmed values', () => {
      component.vendedorForm.setValue({
        nombreCompleto: '  Ana García  ',
        documento: '  12345678  ',
        correoElectronico: '  ana@test.com  ',
        telefono: '  987654321  ',
        zonaAsignada: 'z1'
      });

      const request = component['buildRequest']();

      expect(request.full_name).toBe('Ana García');
      expect(request.doi).toBe('12345678');
      expect(request.email).toBe('ana@test.com');
      expect(request.phone).toBe('987654321');
      expect(request.zone_id).toBe('z1');
    });
  });

  describe('Form markAllAsTouched', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should mark all fields as touched when submitting invalid form', fakeAsync(() => {
      spyOn(component.vendedorForm, 'markAllAsTouched');

      component.crearVendedor();

      expect(component.vendedorForm.markAllAsTouched).toHaveBeenCalled();

      tick(5000);
    }));
  });

  describe('onCreateSuccess', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should reset form and set zonaAsignada to null after success', fakeAsync(() => {
      component.vendedorForm.setValue({
        nombreCompleto: 'Ana García',
        documento: '12345678',
        correoElectronico: 'ana@test.com',
        telefono: '987654321',
        zonaAsignada: 'z1'
      });

      spyOn(component.vendedorForm, 'patchValue').and.callThrough();

      component.crearVendedor();
      tick();

      expect(component.vendedorForm.patchValue).toHaveBeenCalledWith({ zonaAsignada: null });

      tick(5000);
    }));
  });
});
