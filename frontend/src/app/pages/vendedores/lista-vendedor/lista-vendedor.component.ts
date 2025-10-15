// src/app/components/vendedores/lista-vendedor/lista-vendedor.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Vendedor } from '../../../interfaces/vendedor.interface';
import { VendedorService } from '../../../services/vendedores/vendedor.service';

@Component({
  selector: 'app-lista-vendedor',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './lista-vendedor.component.html',
  styleUrls: ['./lista-vendedor.component.css']
})
export class ListaVendedorComponent implements OnInit {
  vendedores: Vendedor[] = [];
  vendedoresFiltrados: Vendedor[] = [];
  isLoading = false;
  error: string | null = null;

  pageSize = 10;
  currentPage = 1;

  constructor(
    private router: Router,
    private vendedorService: VendedorService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.cargarVendedores();
  }

  /**
   * Carga la lista de vendedores desde el API
   */
  cargarVendedores(): void {
    this.isLoading = true;
    this.error = null;

    this.vendedorService.getVendedores().subscribe({
      next: (vendedores) => {
        this.vendedores = vendedores;
        this.vendedoresFiltrados = vendedores;
        this.isLoading = false;

        if (vendedores.length === 0) {
          this.mostrarMensaje('No hay vendedores registrados', 'info');
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.error = 'Error al cargar los vendedores';
        this.mostrarMensaje('Error al cargar los vendedores. Por favor, intente nuevamente.', 'error');
        console.error('Error al cargar vendedores:', error);
      }
    });
  }

  /**
   * Exporta los vendedores a CSV
   */
  exportarCSV(): void {
    if (this.vendedores.length === 0) {
      this.mostrarMensaje('No hay datos para exportar', 'warning');
      return;
    }

    try {
      this.vendedorService.exportToCSV(this.vendedores);
      this.mostrarMensaje('Archivo CSV descargado exitosamente', 'success');
    } catch (error) {
      this.mostrarMensaje('Error al exportar el archivo CSV', 'error');
      console.error('Error al exportar CSV:', error);
    }
  }

  /**
   * Navega a la página de creación de vendedor
   */
  crearVendedor(): void {
    this.router.navigate(['/vendedores/crear']);
  }

  /**
   * Maneja el cambio de tamaño de página
   */
  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize = Number(target.value);
    this.currentPage = 1;
  }

  /**
   * Navega a la página anterior
   */
  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  /**
   * Navega a la página siguiente
   */
  nextPage(): void {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (this.currentPage < totalPages) {
      this.currentPage++;
    }
  }

  /**
   * Muestra mensajes al usuario
   */
  private mostrarMensaje(mensaje: string, tipo: 'success' | 'error' | 'warning' | 'info'): void {
    this.snackBar.open(mensaje, 'Cerrar', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: [`snackbar-${tipo}`]
    });
  }

  /**
   * Obtiene los vendedores paginados
   */
  get vendedoresPaginados(): Vendedor[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.vendedoresFiltrados.slice(startIndex, endIndex);
  }

  /**
   * Calcula el índice del primer elemento
   */
  get startItem(): number {
    if (this.totalItems === 0) return 0;
    return (this.currentPage - 1) * this.pageSize + 1;
  }

  /**
   * Calcula el índice del último elemento
   */
  get endItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.totalItems);
  }

  /**
   * Obtiene el total de elementos
   */
  get totalItems(): number {
    return this.vendedoresFiltrados.length;
  }

  /**
   * Verifica si hay página anterior
   */
  get hasPreviousPage(): boolean {
    return this.currentPage > 1;
  }

  /**
   * Verifica si hay página siguiente
   */
  get hasNextPage(): boolean {
    return this.endItem < this.totalItems;
  }
}
