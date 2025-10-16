import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../../services/login/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    const fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mark form as touched if invalid', () => {
    component.loginForm.setValue({ email: '', password: '' });
    spyOn(component.loginForm, 'markAllAsTouched');

    component.onSubmit();

    expect(component.loginForm.markAllAsTouched).toHaveBeenCalled();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('should call AuthService.login and navigate on success', fakeAsync(() => {
    const mockResponse = { message: 'OTP enviado' };
    authServiceSpy.login.and.returnValue(of(mockResponse));

    component.loginForm.setValue({
      email: 'test@example.com',
      password: '123456'
    });

    component.onSubmit();
    tick();

    expect(authServiceSpy.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: '123456'
    });
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/obtener-acceso']);
    expect(component.isLoading).toBeFalse();
    expect(component.errorMessage).toBeNull();
  }));

  it('should handle 401 error', fakeAsync(() => {
    const mockError = { status: 401 };
    authServiceSpy.login.and.returnValue(throwError(() => mockError));

    component.loginForm.setValue({
      email: 'test@example.com',
      password: 'wrong'
    });

    component.onSubmit();
    tick();

    expect(component.errorMessage).toBe('Correo electrónico o contraseña inválidos.');
    expect(component.isLoading).toBeFalse();
  }));

  it('should handle validation error array from backend', fakeAsync(() => {
    const mockError = { error: { detail: [{ msg: 'Campo requerido' }] } };
    authServiceSpy.login.and.returnValue(throwError(() => mockError));

    component.loginForm.setValue({
      email: 'test@example.com',
      password: '123'
    });

    component.onSubmit();
    tick();

    expect(component.errorMessage).toBe('Campo requerido');
  }));

  it('should handle unexpected error', fakeAsync(() => {
    const mockError = { message: 'Server crash' };
    authServiceSpy.login.and.returnValue(throwError(() => mockError));

    component.loginForm.setValue({
      email: 'test@example.com',
      password: '123456'
    });

    component.onSubmit();
    tick();

    expect(component.errorMessage).toBe('Ocurrió un error inesperado. Por favor, intente de nuevo.');
  }));
});
