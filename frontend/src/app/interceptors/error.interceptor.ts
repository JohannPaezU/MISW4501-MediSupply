import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/login/auth.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (shouldLogout(error, req.url, authService)) {
        authService.logout('expired');
      }

      return throwError(() => error);
    })
  );
};

function shouldLogout(error: HttpErrorResponse, url: string, authService: AuthService): boolean {
  const isUnauthorized = error?.status === 401;
  const isAuthRequest = ['/auth/login', '/auth/verify-otp'].some(path => url.includes(path));
  const hasToken = !!authService.getToken();

  return isUnauthorized && !isAuthRequest && hasToken;
}
