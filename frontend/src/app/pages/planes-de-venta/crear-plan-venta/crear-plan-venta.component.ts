import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-crear-plan-venta',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
  ],
  templateUrl: './crear-plan-venta.component.html',
  styleUrls: ['./crear-plan-venta.component.css']
})
export class CrearPlanVentaComponent {
  planVentaForm: FormGroup;

  periodos: string[] = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Setiembre', 'Octubre', 'Noviembre', 'Diciembre'];
  productos: string[] = ['Medicamentos', 'Equipos Médicos', 'Insumos Quirúrgicos', 'Material de Curación'];
  zonas: string[] = ['Bogotá', 'Medellín', 'Cali', 'Barranquilla', 'Cartagena'];

  constructor(private fb: FormBuilder) {
    this.planVentaForm = this.fb.group({
      periodo: ['Setiembre', Validators.required],
      producto: ['Medicamentos', Validators.required],
      metaUnidades: [1200, [Validators.required, Validators.min(1), Validators.pattern(/^[0-9]+$/)]],
      zona: ['Bogotá', Validators.required],
      vendedorAsociado: ['Miguel Padilla', Validators.required]
    });
  }

  crearPlanDeVenta(): void {
    if (this.planVentaForm.valid) {
      console.log('Plan de Venta a crear:', this.planVentaForm.value);
    } else {
      console.log('Formulario inválido. Revise los campos.');
      this.planVentaForm.markAllAsTouched();
    }
  }
}
