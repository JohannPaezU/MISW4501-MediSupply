import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

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

  constructor(private fb: FormBuilder) {
    this.proveedorForm = this.fb.group({
      nombre: ['', Validators.required],
      numeroRIT: ['', Validators.required],
      ciudad: ['', Validators.required],
      pais: ['', Validators.required],
      correoElectronico: ['', [Validators.required, Validators.email]],
      telefono: ['', Validators.required],
      certificacionesSanitarias: ['', Validators.required]
    });
  }

  crearProveedor(): void {
    if (this.proveedorForm.valid) {
      console.log('Crear proveedor:', this.proveedorForm.value);
    } else {
      console.log('Formulario inválido');
      Object.keys(this.proveedorForm.controls).forEach(key => {
        const control = this.proveedorForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }
}
