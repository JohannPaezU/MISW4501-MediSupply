import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-crear-vendedor',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: './crear-vendedor.component.html',
  styleUrls: ['./crear-vendedor.component.css']
})
export class CrearVendedorComponent {
  vendedorForm: FormGroup;

  zonas: string[] = [
    'Bogotá',
    'Medellín',
    'Cali',
    'Barranquilla',
    'Cartagena'
  ];

  constructor(private fb: FormBuilder) {
    this.vendedorForm = this.fb.group({
      nombreCompleto: ['', Validators.required],
      documento: ['', Validators.required],
      correoElectronico: ['', [Validators.required, Validators.email]],
      telefono: ['', Validators.required],
      zonaAsignada: ['', Validators.required]
    });
  }

  importarDesdeCSV(): void {
    console.log('Importar desde CSV');
  }

  crearVendedor(): void {
    if (this.vendedorForm.valid) {
      console.log('Crear vendedor:', this.vendedorForm.value);
    } else {
      console.log('Formulario inválido');
      Object.keys(this.vendedorForm.controls).forEach(key => {
        const control = this.vendedorForm.get(key);
        if (control?.invalid) {
          control.markAsTouched();
        }
      });
    }
  }
}
