import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { Zone, CreateVendedorRequest } from '../../../interfaces/vendedor.interface';
import { VendedorService } from '../../../services/vendedores/vendedor.service';
import { LoadingSpinnerComponent } from '../../../components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-crear-vendedor',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    LoadingSpinnerComponent
  ],
  templateUrl: './crear-vendedor.component.html',
  styleUrls: ['./crear-vendedor.component.css']
})
export class CrearVendedorComponent implements OnInit {
  vendedorForm: FormGroup;
  zonas: Zone[] = [];
  isLoading = false;
  isLoadingZones = true;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private fb: FormBuilder,
    private vendedorService: VendedorService,
    private cdr: ChangeDetectorRef
  ) {
    this.vendedorForm = this.fb.group({
      nombreCompleto: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(100)]],
      documento: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
      correoElectronico: ['', [Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(120)]],
      telefono: ['', [Validators.required, Validators.pattern(/^\d{9,15}$/)]],
      zonaAsignada: [null, Validators.required]
    });
  }

  get nombreCompleto() { return this.vendedorForm.get('nombreCompleto'); }
  get documento() { return this.vendedorForm.get('documento'); }
  get correoElectronico() { return this.vendedorForm.get('correoElectronico'); }
  get telefono() { return this.vendedorForm.get('telefono'); }
  get zonaAsignada() { return this.vendedorForm.get('zonaAsignada'); }

  ngOnInit(): void {
    this.cargarZonas();
  }

  cargarZonas(): void {
    this.isLoadingZones = true;
    this.vendedorService.getZonas()
      .pipe(finalize(() => {
        this.isLoadingZones = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (data) => {
          this.zonas = data;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cargar las zonas:', err);
          this.showToast('Error al cargar las zonas. Intente de nuevo.', 'error');
        }
      });
  }

  private buildRequest(): CreateVendedorRequest {
    const formValue = this.vendedorForm.value;
    return {
      full_name: formValue.nombreCompleto.trim(),
      doi: formValue.documento.trim(),
      email: formValue.correoElectronico.trim(),
      phone: formValue.telefono.trim(),
      zone_id: formValue.zonaAsignada
    };
  }

  private handleError(err: any): string {
    const messages: Record<number, string> = {
      409: 'Ya existe un vendedor con este correo o documento.',
      422: 'La zona seleccionada no existe.'
    };

    if (messages[err.status]) return messages[err.status];

    const detail = err?.error?.detail;
    if (typeof detail === 'string') return detail;
    if (Array.isArray(detail)) return detail[0]?.msg || 'Error desconocido.';

    return 'Hubo un error al crear el vendedor. Por favor, intenta de nuevo.';
  }

  crearVendedor(): void {
    this.vendedorForm.markAllAsTouched();

    if (this.vendedorForm.invalid) {
      this.showToast('Por favor, complete todos los campos correctamente.', 'error');
      return;
    }

    this.isLoading = true;
    const nuevoVendedor = this.buildRequest();

    this.vendedorService.createVendedor(nuevoVendedor)
      .pipe(finalize(() => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (response) => this.onCreateSuccess(response),
        error: (err) => this.onCreateError(err)
      });
  }

  private onCreateSuccess(response: any): void {
    this.showToast(`¡Vendedor ${response.full_name} creado con éxito!`, 'success');
    this.vendedorForm.reset();
    this.vendedorForm.patchValue({ zonaAsignada: null });
    this.cdr.detectChanges();
  }

  private onCreateError(err: any): void {
    console.error('Error al crear el vendedor:', err);
    const errorMessage = this.handleError(err);
    this.showToast(errorMessage, 'error');
  }
  importarDesdeCSV(): void {
    this.showToast('Funcionalidad próximamente disponible', 'error');
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
