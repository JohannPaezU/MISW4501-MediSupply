import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ProductCreateResponse } from '../../../interfaces/producto.interface';
import { Zone, Vendedor } from '../../../interfaces/vendedor.interface';
import { ProductService } from '../../../services/productos/product.service';
import { VendedorService } from '../../../services/vendedores/vendedor.service';
import { CreatePlanVentaRequest } from '../../../interfaces/planVenta.interface';
import { PlanVentaService } from '../../../services/planes-de-venta/planesDeVenta.service';

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
export class CrearPlanVentaComponent implements OnInit {
  planVentaForm: FormGroup;

  periodos: string[] = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
  productos: ProductCreateResponse[] = [];
  zonas: Zone[] = [];
  vendedores: Vendedor[] = [];
  vendedoresFiltrados: Vendedor[] = [];

  isLoading = false;
  isLoadingData = true;
  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private fb: FormBuilder,
    private planVentaService: PlanVentaService,
    private productService: ProductService,
    private vendedorService: VendedorService,
    private cdr: ChangeDetectorRef
  ) {
    this.planVentaForm = this.fb.group({
      periodo: [null, Validators.required],
      producto: [null, Validators.required],
      metaUnidades: [null, [Validators.required, Validators.min(1)]],
      zona: [null, Validators.required],
      vendedorAsociado: [null, Validators.required],
      searchVendedor: ['']
    });
  }

  get periodo() { return this.planVentaForm.get('periodo'); }
  get producto() { return this.planVentaForm.get('producto'); }
  get metaUnidades() { return this.planVentaForm.get('metaUnidades'); }
  get zona() { return this.planVentaForm.get('zona'); }
  get vendedorAsociado() { return this.planVentaForm.get('vendedorAsociado'); }
  get searchVendedor() { return this.planVentaForm.get('searchVendedor'); }

  ngOnInit(): void {
    this.cargarDatos();

    this.searchVendedor?.valueChanges.subscribe(searchTerm => {
      this.filtrarVendedores(searchTerm);
    });
  }

  cargarDatos(): void {
    this.isLoadingData = true;

    Promise.all([
      this.productService.getProducts(1, 1000).toPromise(),
      this.vendedorService.getZonas().toPromise(),
      this.vendedorService.getVendedores().toPromise()
    ]).then(([productosRes, zonasRes, vendedoresRes]) => {
      this.productos = productosRes?.products || [];
      this.zonas = zonasRes || [];
      this.vendedores = vendedoresRes?.sellers || [];
      this.vendedoresFiltrados = this.vendedores;
      this.isLoadingData = false;
      this.cdr.detectChanges();
    }).catch(err => {
      console.error('Error al cargar datos:', err);
      this.showToast('Error al cargar los datos necesarios', 'error');
      this.isLoadingData = false;
      this.cdr.detectChanges();
    });
  }

  filtrarVendedores(searchTerm: string): void {
    if (!searchTerm || searchTerm.trim() === '') {
      this.vendedoresFiltrados = this.vendedores;
    } else {
      const term = searchTerm.toLowerCase().trim();
      this.vendedoresFiltrados = this.vendedores.filter(v =>
        v.full_name.toLowerCase().includes(term) ||
        v.doi.toLowerCase().includes(term)
      );
    }
    this.cdr.detectChanges();
  }

  crearPlanDeVenta(): void {
    this.planVentaForm.markAllAsTouched();

    if (this.planVentaForm.invalid) {
      this.showToast('Por favor, complete todos los campos correctamente.', 'error');
      return;
    }

    this.isLoading = true;
    const nuevoPlan = this.construirRequest(this.planVentaForm.value);

    this.planVentaService.createPlanVenta(nuevoPlan)
      .pipe(finalize(() => this.finalizarCarga()))
      .subscribe({
        next: () => this.procesarExito(),
        error: (err) => this.procesarError(err)
      });
  }

  private construirRequest(formValue: any): CreatePlanVentaRequest {
    return {
      period: formValue.periodo,
      goal: Number(formValue.metaUnidades),
      product_id: formValue.producto,
      zone_id: formValue.zona,
      seller_id: formValue.vendedorAsociado
    };
  }

  private procesarExito(): void {
    this.showToast('¡Plan de venta creado con éxito!', 'success');
    this.planVentaForm.reset();
    this.searchVendedor?.setValue('');
    this.vendedoresFiltrados = this.vendedores;
    this.cdr.detectChanges();
  }

  private procesarError(err: any): void {
    console.error('Error al crear plan de venta:', err);
    const mensaje = this.obtenerMensajeDeError(err);
    this.showToast(mensaje, 'error');
  }

  private obtenerMensajeDeError(err: any): string {
    const statusMessages: Record<number, string> = {
      409: 'Ya existe un plan de venta con estos mismos datos.',
      422: 'Uno de los datos seleccionados no es válido.'
    };

    if (statusMessages[err.status]) {
      return statusMessages[err.status];
    }

    const detail = err?.error?.detail;

    if (typeof detail === 'string') {
      return detail;
    }

    if (Array.isArray(detail)) {
      return detail[0]?.msg || 'Error desconocido.';
    }

    return 'Hubo un error al crear el plan de venta.';
  }

  private finalizarCarga(): void {
    this.isLoading = false;
    this.cdr.detectChanges();
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
