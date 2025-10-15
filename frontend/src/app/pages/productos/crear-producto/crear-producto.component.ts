import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';

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

  constructor(private fb: FormBuilder, private router: Router) {
    this.productoForm = this.fb.group({
      nombreProducto: ['', Validators.required],
      numeroLote: ['', Validators.required],
      bodega: ['', Validators.required],
      imagenUrl: ['', Validators.required],
      fechaVencimiento: ['', Validators.required],
      fichaTecnica: [''],
      stock: ['', [Validators.required, Validators.min(1)]],
      precioUnidad: ['', [Validators.required, Validators.min(0)]],
      proveedor: ['', Validators.required]
    });
  }

  crearProductoMasivo(): void {
    this.router.navigate(['/productos/crear-masivo']);
  }

  crearProducto(): void {
    if (this.productoForm.valid) {
      console.log('Crear producto:', this.productoForm.value);
    } else {
      console.log('Formulario inválido');
      Object.keys(this.productoForm.controls).forEach(key => {
        const control = this.productoForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }
}
