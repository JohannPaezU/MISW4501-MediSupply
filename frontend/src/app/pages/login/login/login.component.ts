import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../services/login/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string | null = null;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.loginForm = this.fb.group({
      email: ['js.cervantes@uniandes.edu.co', [Validators.required, Validators.email]],
      password: ['123456', [Validators.required, Validators.maxLength(12)]],
    });
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;

    this.authService.login(this.loginForm.value).pipe(
      finalize(() => this.isLoading = false)
    ).subscribe({
      next: (response) => {
        console.log(response.message);
        // Redirigir a la página para verificar el OTP
        this.router.navigate(['/obtener-acceso']);
      },
      error: (err) => {
        if (err.status === 401) {
          this.errorMessage = 'Correo electrónico o contraseña inválidos.';
        } else if (err.error && Array.isArray(err.error.detail)) {
          this.errorMessage = err.error.detail[0].msg;
        } else {
          this.errorMessage = 'Ocurrió un error inesperado. Por favor, intente de nuevo.';
        }
      }
    });
  }
}
