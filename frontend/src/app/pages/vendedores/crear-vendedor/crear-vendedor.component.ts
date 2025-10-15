import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
// ¡Importante! Añadir MatSnackBar
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { finalize } from 'rxjs';
import { Zone, CreateVendedorRequest } from '../../../interfaces/vendedor.interface';
import { CrearVendedorService } from '../../../services/vendedores/crearVendedor.service';

@Component({
  selector: 'app-crear-vendedor',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatSelectModule,
    MatSnackBarModule // Añadir el módulo aquí
  ],
  templateUrl: './crear-vendedor.component.html',
  styleUrls: ['./crear-vendedor.component.css']
})
export class CrearVendedorComponent implements OnInit {
  vendedorForm: FormGroup;
  zonas: Zone[] = [];
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private crearVendedorService: CrearVendedorService,
    private snackBar: MatSnackBar // Inyectar MatSnackBar
  ) {
    this.vendedorForm = this.fb.group({
      nombreCompleto: ['', Validators.required],
      documento: ['', Validators.required],
      correoElectronico: ['', [Validators.required, Validators.email]],
      telefono: ['', Validators.required],
      zonaAsignada: [null, Validators.required] // Usar null como valor inicial para el select
    });
  }

  // Getters para acceder fácilmente a los controles del formulario en el HTML
  get nombreCompleto() { return this.vendedorForm.get('nombreCompleto'); }
  get documento() { return this.vendedorForm.get('documento'); }
  get correoElectronico() { return this.vendedorForm.get('correoElectronico'); }
  get telefono() { return this.vendedorForm.get('telefono'); }
  get zonaAsignada() { return this.vendedorForm.get('zonaAsignada'); }

  ngOnInit(): void {
    this.cargarZonas();
  }

  cargarZonas(): void {
    this.crearVendedorService.getZonas().subscribe({
      next: (data) => {
        this.zonas = data;
      },
      error: (err) => {
        this.mostrarMensaje('Error al cargar las zonas. Intente de nuevo.', 'error');
        console.error('Error al cargar las zonas:', err);
      }
    });
  }

  crearVendedor(): void {
    this.vendedorForm.markAllAsTouched();

    if (this.vendedorForm.invalid) {
      this.mostrarMensaje('Por favor, complete todos los campos requeridos.', 'error');
      return;
    }

    this.isLoading = true;

    const formValue = this.vendedorForm.value;
    const nuevoVendedor: CreateVendedorRequest = {
      fullname: formValue.nombreCompleto,
      document: formValue.documento,
      email: formValue.correoElectronico,
      phone: formValue.telefono,
      zone_id: Number(formValue.zonaAsignada)
    };

    this.crearVendedorService.createVendedor(nuevoVendedor)
      .pipe(finalize(() => this.isLoading = false))
      .subscribe({
        next: (response) => {
          this.mostrarMensaje(`¡Vendedor ${response.fullname} creado con éxito!`, 'success');
          this.vendedorForm.reset();
        },
        error: (err) => {
          console.error('Error al crear el vendedor:', err);
          this.mostrarMensaje('Hubo un error al crear el vendedor. Por favor, intenta de nuevo.', 'error');
        }
    });
  }

  importarDesdeCSV(): void {
    // Lógica futura para la importación
  }

  /**
   * Muestra mensajes al usuario usando MatSnackBar
   */
  private mostrarMensaje(mensaje: string, tipo: 'success' | 'error'): void {
    this.snackBar.open(mensaje, 'Cerrar', {
      duration: 5000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: [`snackbar-${tipo}`] // Clases CSS para estilizar el snackbar
    });
  }
}
