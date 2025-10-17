import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { finalize, timer } from 'rxjs';

import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { ProviderCreateRequest } from '../../../interfaces/proveedor.intrface';

@Component({
  selector: 'app-crear-proveedor',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './crear-proveedor.component.html',
  styleUrls: ['./crear-proveedor.component.css']
})
export class CrearProveedorComponent {
  proveedorForm: FormGroup;
  isLoading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  toastMessage: string | null = null;
  toastType: 'success' | 'error' | null = null;

  get f() { return this.proveedorForm.controls; }

   constructor(private fb: FormBuilder, private proveedorService: ProveedorService, private cdr: ChangeDetectorRef) {

    this.proveedorForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      rit: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
      city: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      country: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
      phone: ['', [Validators.required, Validators.minLength(9), Validators.maxLength(15), Validators.pattern('^[0-9]*$')]],
      image_url: ['', [Validators.maxLength(255)]]
    });
  }

  crearProveedor(): void {
    if (this.proveedorForm.invalid) {
      this.proveedorForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.successMessage = null;
    this.errorMessage = null;

    const providerData: ProviderCreateRequest = this.proveedorForm.value;

    this.proveedorService.createProvider(providerData).pipe(
      finalize(() => {
        this.isLoading = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (response) => {
        const message = `¡Proveedor "${response.name}" creado con éxito!`;
        this.successMessage = message;
        this.showToast('Proveedor creado exitosamente', 'success');
        this.proveedorForm.reset();
      },
      error: (err) => {
        console.error(err);
        let message = 'Ocurrió un error inesperado al crear el proveedor.';
        if (err.status === 409) {
          message = 'Ya existe un proveedor con este correo electrónico o RIT.';
        } else if (err.status === 422 && err.error && err.error.detail) {
          const firstError = err.error.detail[0];
          const field = firstError.loc[1];
          const errorMsg = firstError.msg;
          message = `Error en el campo '${field}': ${errorMsg}`;
        }
        this.errorMessage = message;
        this.showToast(message, 'error');
      }
    });
  }

  private showToast(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.cdr.detectChanges();

    timer(5000).subscribe(() => {
      this.toastMessage = null;
      this.toastType = null;
      this.cdr.detectChanges();
    });
  }
}

