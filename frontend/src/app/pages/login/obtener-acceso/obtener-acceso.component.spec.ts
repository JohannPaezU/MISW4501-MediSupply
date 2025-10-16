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

  it('debe llamar a verifyOtp correctamente al enviar un código válido', () => {
    component.userEmail = 'test@mail.com';
    component.otpForm.setValue({ otp_code: '123456' });

    component.onSubmit();

    expect(authServiceMock.verifyOtp).toHaveBeenCalledWith('test@mail.com', '123456');
  });

  it('debe manejar éxito en la verificación OTP y redirigir a /home', (done) => {
    spyOn(component as any, 'handleSuccess').and.callThrough();
    component.userEmail = 'test@mail.com';
    component.otpForm.setValue({ otp_code: '123456' });

    component.onSubmit();

    setTimeout(() => {
      expect((component as any).handleSuccess).toHaveBeenCalled();
      expect(component.successMessage).toBe('Verificado correctamente');
      expect(routerMock.navigate).toHaveBeenCalledWith(['/home']);
      done();
    }, 800);
  });

  it('debe manejar error en verifyOtp y mostrar mensaje adecuado', () => {
    authServiceMock.verifyOtp.and.returnValue(throwError(() => ({ status: 401, message: 'OTP inválido' })));

    component.userEmail = 'test@mail.com';
    component.otpForm.setValue({ otp_code: '000000' });

    component.onSubmit();

    expect(component.errorMessage).toBe('OTP inválido');
    expect(component.isLoading).toBeFalse();
  });
});
