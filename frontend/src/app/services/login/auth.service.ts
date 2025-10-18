import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginResponse, VerifyOtpResponse } from '../../interfaces/login.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private tokenKey = 'access_token';
  private emailKey = 'user_email';
  private sessionExpiredKey = 'session_expired';

  private emailForOtp = new BehaviorSubject<string | null>(null);
  public emailForOtp$ = this.emailForOtp.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: { email: string; password: string }): Observable<LoginResponse> {
    this.clearSessionExpiredFlag();

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
          this.saveSession(response.access_token, email);
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

  logout(reason: 'manual' | 'expired' = 'manual'): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.emailKey);

    if (reason === 'expired') {
      localStorage.setItem(this.sessionExpiredKey, 'true');
    }

    this.router.navigate(['/login']);
  }

  isSessionExpired(): boolean {
    return localStorage.getItem(this.sessionExpiredKey) === 'true';
  }

  clearSessionExpiredFlag(): void {
    localStorage.removeItem(this.sessionExpiredKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}
