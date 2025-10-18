import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { ProveedorService } from '../../../services/proveedores/proveedor.service';
import { ProviderBase } from '../../../interfaces/proveedor.intrface';
import { CsvExportService } from '../../../services/utilities/csv.service';

@Component({
  selector: 'app-lista-proveedor',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './lista-proveedor.component.html',
  styleUrls: ['./lista-proveedor.component.css']
})
export class ListaProveedorComponent implements OnInit {
  proveedores: ProviderBase[] = [];
  isLoading = true;
  errorMessage: string | null = null;
  copiedId: string | null = null;

  pageSize = 10;
  currentPage = 1;
  totalItems = 0;

  constructor(
    private router: Router,
    private proveedorService: ProveedorService,
    private cdr: ChangeDetectorRef,
    private csvExportService: CsvExportService
  ) { }

  ngOnInit(): void {
    this.cargarProveedores();
  }

  cargarProveedores(): void {
    this.isLoading = true;
    this.errorMessage = null;

    this.proveedorService.getProviders().pipe(
      finalize(() => {
        this.isLoading = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (response) => {
        this.proveedores = response.providers;
        this.totalItems = response.total_count;
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'No se pudieron cargar los proveedores. Intente de nuevo más tarde.';
      }
    });
  }

  exportarCSV(): void {
    const headers = {
      id: 'ID',
      name: 'Nombre',
      rit: 'RIT',
      city: 'Ciudad',
      country: 'País',
      email: 'Correo Electrónico',
      phone: 'Teléfono',
      created_at: 'Fecha de Creación'
    };
    this.csvExportService.exportarACsv(this.proveedores, 'lista-proveedores', headers);
  }

  copyToClipboard(id: string): void {
    navigator.clipboard.writeText(id).then(() => {
      this.copiedId = id;
      this.cdr.detectChanges();
      setTimeout(() => {
        this.copiedId = null;
        this.cdr.detectChanges();
      }, 1500);
    }).catch(err => console.error('Error al copiar ID: ', err));
  }

  crearProveedor(): void {
    this.router.navigate(['/proveedores/crear']);
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize = Number(target.value);
    this.currentPage = 1;
  }

  previousPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  nextPage(): void {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (this.currentPage < totalPages) {
      this.currentPage++;
    }
  }

  get paginatedProveedores(): ProviderBase[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.proveedores.slice(startIndex, startIndex + this.pageSize);
  }

  get startItem(): number {
    return this.proveedores.length > 0 ? (this.currentPage - 1) * this.pageSize + 1 : 0;
  }

  get endItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.totalItems);
  }
}

