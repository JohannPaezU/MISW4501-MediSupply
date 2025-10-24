import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { MatIconModule } from '@angular/material/icon';
import { ProductCreateRequest } from '../../../interfaces/producto.interface';
import { ProductService } from '../../../services/productos/product.service';


@Component({
  selector: 'app-crear-producto',
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
  templateUrl: './crear-producto.component.html',
  styleUrls: ['./crear-producto.component.css']
})
export class CrearProductoComponent {
  productoForm: FormGroup;
  isLoading = false;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  get f() { return this.productoForm.controls; }

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private productService: ProductService,
    private cdr: ChangeDetectorRef
  ) {
    this.productoForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      batch: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      store: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      image_url: [null, [Validators.maxLength(300)]],
      due_date: ['', Validators.required],
      details: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      stock: [null, [Validators.required, Validators.min(1)]],
      price_per_unit: [null, [Validators.required, Validators.min(0.01)]],
      provider_id: ['', [Validators.required, Validators.minLength(36), Validators.maxLength(36)]]
    });
  }

  crearProductoMasivo(): void {
    this.router.navigate(['/productos/crear-masivo']);
  }

  crearProducto(): void {
    if (this.formInvalido()) return;

    this.toggleLoading(true);

    const productData: ProductCreateRequest = this.productoForm.value;

    this.productService.createProduct(productData).pipe(
      finalize(() => this.toggleLoading(false))
    ).subscribe({
      next: (response) => this.onProductoCreado(response),
      error: (err) => this.onError(err)
    });
  }

  private formInvalido(): boolean {
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      return true;
    }
    return false;
  }

  private toggleLoading(state: boolean): void {
    this.isLoading = state;
    this.cdr.detectChanges();
  }

  private onProductoCreado(response: any): void {
    this.showToast(`¡Producto "${response.name}" creado con éxito!`, 'success');
    this.productoForm.reset();
  }

  private onError(err: any): void {
    console.error('Error Response:', err);
    const errorMessage = this.obtenerMensajeError(err);
    this.showToast(errorMessage, 'error');
  }

  private obtenerMensajeError(err: any): string {
    if (this.isValidationError(err)) {
      const firstError = err.error.detail[0];
      const field = firstError.loc?.[1] ?? 'campo desconocido';
      const message = firstError.msg ?? 'error desconocido';
      return `Error en el campo '${field}': ${message}`;
    }

    if (typeof err?.error?.message === 'string') {
      return err.error.message;
    }

    return 'Ocurrió un error inesperado.';
  }

  private isValidationError(err: any): boolean {
    return err?.status === 422 && Array.isArray(err?.error?.detail) && err.error.detail.length > 0;
  }
  showToast(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.toastMessage = null;
      this.cdr.detectChanges();
    }, 5000);
  }
}

