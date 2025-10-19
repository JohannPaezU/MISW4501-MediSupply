import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ObtenerAccesoComponent } from './obtener-acceso.component';
import { AuthService } from '../../../services/login/auth.service';
import { Router } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

describe('ObtenerAccesoComponent', () => {
  let component: ObtenerAccesoComponent;
  let fixture: ComponentFixture<ObtenerAccesoComponent>;
  let authServiceMock: any;
  let routerMock: any;
  let emailSubject: Subject<string | null>;
  let cdr: ChangeDetectorRef;

  beforeEach(async () => {
    emailSubject = new Subject<string | null>();

    authServiceMock = {
      emailForOtp$: emailSubject.asObservable(),
      verifyOtp: jasmine.createSpy('verifyOtp').and.returnValue(of({ message: 'Verificado correctamente' }))
    };

    routerMock = { navigate: jasmine.createSpy('navigate') };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, ObtenerAccesoComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        ChangeDetectorRef
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ObtenerAccesoComponent);
    component = fixture.componentInstance;
    cdr = TestBed.inject(ChangeDetectorRef);
    fixture.detectChanges();
  });

  it('debe redirigir al login si no hay email en el observable', () => {
    emailSubject.next(null);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('debe asignar el email si existe', () => {
    emailSubject.next('test@mail.com');
    expect(component.userEmail).toBe('test@mail.com');
  });

  it('debe actualizar otpDisplay correctamente', () => {
    component.updateOtpDisplay('123');
    expect(component.otpDisplay[0]).toBe('1');
    expect(component.otpDisplay[3]).toBeNull();
  });

  it('focusInput debe llamar a focus en el input', () => {
    const input = { focus: jasmine.createSpy('focus') } as any;
    component.focusInput(input);
    expect(input.focus).toHaveBeenCalled();
  });

  it('no debe llamar a verifyOtp si el formulario es inválido o falta email', () => {
    component.userEmail = null;
    component.otpForm.setValue({ otp_code: '' });
    component.onSubmit();
    expect(authServiceMock.verifyOtp).not.toHaveBeenCalled();
  });

  it('debe llamar a verifyOtp correctamente al enviar un código válido', () => {
    component.userEmail = 'test@mail.com';
    component.otpForm.setValue({ otp_code: '123456' });
    component.onSubmit();
    expect(authServiceMock.verifyOtp).toHaveBeenCalledWith('test@mail.com', '123456');
  });

  it('debe manejar éxito en la verificación OTP y redirigir a /home', (done) => {
    component.userEmail = 'test@mail.com';
    component.otpForm.setValue({ otp_code: '123456' });
    component.onSubmit();
    setTimeout(() => {
      expect(component.successMessage).toBe('Verificado correctamente');
      expect(routerMock.navigate).toHaveBeenCalledWith(['/home']);
      done();
    }, 800);
  });

  it('debe manejar error con status 401', () => {
    authServiceMock.verifyOtp.and.returnValue(throwError(() => ({ status: 401, message: 'OTP inválido' })));
    component.userEmail = 'test@mail.com';
    component.otpForm.setValue({ otp_code: '000000' });
    component.onSubmit();
    expect(component.errorMessage).toBe('OTP inválido');
  });

  it('debe manejar error con status 0 (sin conexión)', () => {
    (component as any).handleError({ status: 0 });
    expect(component.errorMessage).toContain('No se pudo conectar');
  });

  it('debe manejar error genérico sin status ni mensaje', () => {
    (component as any).handleError({});
    expect(component.errorMessage).toContain('Ocurrió un error inesperado');
  });

  it('debe manejar error con string', () => {
    const result = (component as any).parseError('Error plano');
    expect(result.message).toBe('Error plano');
  });

  it('getErrorMessage debe retornar mensaje personalizado según código', () => {
    expect((component as any).getErrorMessage(401, 'Inválido')).toBe('Inválido');
    expect((component as any).getErrorMessage(0, null)).toContain('No se pudo conectar');
    expect((component as any).getErrorMessage(500, null)).toContain('Ocurrió un error');
  });

  it('resetOtpInput debe limpiar el código y el display', () => {
    component.otpForm.setValue({ otp_code: '123456' });
    component.otpDisplay = ['1', '2', '3', '4', '5', '6'];
    (component as any).resetOtpInput();
    expect(component.otpCode?.value).toBe('');
    expect(component.otpDisplay.every(x => x === null)).toBeTrue();
  });

  it('ngOnDestroy debe desuscribirse del emailSubscription', () => {
    const subSpy = jasmine.createSpyObj('Subscription', ['unsubscribe']);
    (component as any).emailSubscription = subSpy;
    component.ngOnDestroy();
    expect(subSpy.unsubscribe).toHaveBeenCalled();
  });
});
