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

      flush();
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

      flush();
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

      flush();
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

      flush();
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
      flush();
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
      flush();
    }));
  });


  describe('Other Methods', () => {
    it('should show toast for importarDesdeCSV', fakeAsync(() => {
      component.importarDesdeCSV();
      expect(component.toastMessage).toBe('Funcionalidad próximamente disponible');
      expect(component.toastType).toBe('error');
      flush();
    }));
  });
});
