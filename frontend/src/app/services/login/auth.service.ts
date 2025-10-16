import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoginResponse {
  message: string;
  otp_expiration_minutes?: number;
}

export interface VerifyOtpResponse {
  message: string;
  access_token?: string;
  token_type?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private tokenKey = 'access_token';
  private emailKey = 'user_email';

  private emailForOtp = new BehaviorSubject<string | null>(null);
  public emailForOtp$ = this.emailForOtp.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: { email: string; password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, credentials).pipe(
      tap(() => {
        this.emailForOtp.next(credentials.email);
      })
    );
  }

  verifyOtp(email: string, otp_code: string): Observable<VerifyOtpResponse> {
    return this.http.post<VerifyOtpResponse>(`${this.apiUrl}/auth/verify-otp`, { email, otp_code }).pipe(
      tap(response => {
        if (response.access_token) {
          this.saveSession(response.access_token, email); // 🔹 Guardamos ambos
        }
      })
    );
  }

  private saveSession(token: string, email: string): void {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.emailKey, email);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getEmail(): string | null {
    return localStorage.getItem(this.emailKey);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.emailKey);
    this.router.navigate(['/login']);
  }


  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
