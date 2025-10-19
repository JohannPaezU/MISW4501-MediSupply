import { Component, OnDestroy, OnInit, ChangeDetectorRef, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, finalize } from 'rxjs';
import { AuthService } from '../../../services/login/auth.service';

@Component({
  selector: 'app-obtener-acceso',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './obtener-acceso.component.html',
  styleUrls: ['./obtener-acceso.component.css']
})
export class ObtenerAccesoComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('otpInput') otpInputRef!: ElementRef<HTMLInputElement>;

  otpForm: FormGroup;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  isLoading = false;
  userEmail: string | null = null;
  private emailSubscription!: Subscription;

  otpDisplay: (string | null)[] = Array(6).fill(null);
  get otpCode() { return this.otpForm.get('otp_code'); }

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.otpForm = this.fb.group({
      otp_code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]],
    });
  }

  ngOnInit(): void {
    this.emailSubscription = this.authService.emailForOtp$.subscribe(email => {
      if (!email) {
        this.router.navigate(['/login']);
      } else {
        this.userEmail = email;
      }
    });

    this.otpCode?.valueChanges.subscribe((value: string) => this.updateOtpDisplay(value));
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.otpInputRef?.nativeElement.focus();
    }, 100);
  }

  updateOtpDisplay(value: string): void {
    for (let i = 0; i < 6; i++) {
      this.otpDisplay[i] = value[i] || null;
    }
  }

  focusInput(inputElement: HTMLInputElement): void {
    inputElement.focus();
  }

  onSubmit() {
    if (this.isInvalidForm()) return;

    this.startLoading();

    const { otp_code } = this.otpForm.value;

    this.authService.verifyOtp(this.userEmail!, otp_code)
      .pipe(finalize(() => this.stopLoading()))
      .subscribe({
        next: (response) => this.handleSuccess(response),
        error: (err) => this.handleError(err)
      });
  }

  private isInvalidForm(): boolean {
    return this.otpForm.invalid || !this.userEmail;
  }

  private startLoading(): void {
    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;
    this.cdr.detectChanges();
  }

  private stopLoading(): void {
    this.isLoading = false;
    try { this.cdr.detectChanges(); } catch { }
  }

  private handleSuccess(response: any): void {
    const message = response?.message ?? 'Verificado';
    this.successMessage = message;
    this.errorMessage = null;
    this.cdr.detectChanges();

    setTimeout(() => this.router.navigate(['/home']), 700);
  }

  private handleError(err: any): void {
    console.error('Error verificación OTP:', err);

    const { status, message } = this.parseError(err);
    this.errorMessage = this.getErrorMessage(status, message);
    this.resetOtpInput();
    this.updateView();
  }

  private parseError(err: any): { status: number | null; message: string | null } {
    if (!err) return { status: null, message: 'Error desconocido' };

    const status =
      err.status ??
      err.statusCode ??
      err.error?.status ??
      null;

    const message =
      err.error?.message ??
      err.message ??
      (typeof err === 'string' ? err : null);

    return { status, message };
  }

  private updateView(): void {
    try { this.cdr.detectChanges(); } catch { }
  }

  private getErrorMessage(status: number | null, msg: string | null): string {
    if (status === 400 || status === 401) {
      return msg ?? 'El código OTP es inválido o ha expirado.';
    }
    if (status === 0) {
      return 'No se pudo conectar con el servidor. Verifica tu conexión.';
    }
    return msg ?? 'Ocurrió un error inesperado. Intenta de nuevo.';
  }

  private resetOtpInput(): void {
    this.otpCode?.setValue('');
    this.otpDisplay.fill(null);
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    if (this.emailSubscription) this.emailSubscription.unsubscribe();
  }
}
