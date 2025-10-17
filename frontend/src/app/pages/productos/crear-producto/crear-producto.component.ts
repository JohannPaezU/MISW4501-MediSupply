import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
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
    MatButtonModule
  ],
  templateUrl: './crear-producto.component.html',
  styleUrls: ['./crear-producto.component.css']
})
export class CrearProductoComponent {
  productoForm: FormGroup;
  isLoading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // Getter para un acceso más fácil a los controles del formulario en la plantilla
  get f() { return this.productoForm.controls; }

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private productService: ProductService
  ) {
    this.productoForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      batch: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      store: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      image_url: [null, [Validators.maxLength(300)]],
      due_date: ['', Validators.required],
      details: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
      stock: [null, [Validators.required, Validators.min(1)]],
      price_per_unite: [null, [Validators.required, Validators.min(0.01)]],
      provider_id: ['', [Validators.required, Validators.minLength(36), Validators.maxLength(36)]]
    });
  }

  crearProductoMasivo(): void {
    this.router.navigate(['/productos/crear-masivo']);
  }

  crearProducto(): void {
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.successMessage = null;
    this.errorMessage = null;

    const productData: ProductCreateRequest = this.productoForm.value;

    this.productService.createProduct(productData).pipe(
      finalize(() => this.isLoading = false)
    ).subscribe({
      next: (response) => {
        this.successMessage = `¡Producto "${response.name}" creado exitosamente!`;
        this.productoForm.reset();
      },
      error: (err) => {
        console.error('Error Response:', err);
        // Lógica mejorada para interpretar errores de validación de FastAPI
        if (err.status === 422 && err.error && err.error.detail) {
          const firstError = err.error.detail[0];
          const field = firstError.loc[1];
          const message = firstError.msg;
          this.errorMessage = `Error en el campo '${field}': ${message}`;
        } else if (err.error && typeof err.error.message === 'string') {
          this.errorMessage = err.error.message;
        } else {
          this.errorMessage = 'Ocurrió un error inesperado al crear el producto.';
        }
      }
    });
  }
}

