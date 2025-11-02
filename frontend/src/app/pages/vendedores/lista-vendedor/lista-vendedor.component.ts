import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { Vendedor } from '../../../interfaces/vendedor.interface';
import { VendedorService } from '../../../services/vendedores/vendedor.service';
import { CsvExportService } from '../../../services/utilities/csv.service';
import { LoadingSpinnerComponent } from '../../../components/loading-spinner/loading-spinner.component';
import { ItemDialogComponent } from '../../../components/item-dialog/item-dialog.component';

@Component({
  selector: 'app-lista-vendedor',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    LoadingSpinnerComponent,
    ItemDialogComponent
  ],
  templateUrl: './lista-vendedor.component.html',
  styleUrls: ['./lista-vendedor.component.css']
})
export class ListaVendedorComponent implements OnInit {
  vendedores: Vendedor[] = [];
  vendedoresFiltrados: Vendedor[] = [];
  isLoading = false;
  error: string | null = null;
  copiedId: string | null = null;
  dialogVisible = false;
  selectedItem: any = null;

  pageSize = 10;
  currentPage = 1;

  toastMessage: string | null = null;
  toastType: 'success' | 'error' = 'success';

  constructor(
    private router: Router,
    private vendedorService: VendedorService,
    private csvExportService: CsvExportService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarVendedores();
  }

  cargarVendedores(): void {
    this.isLoading = true;
    this.error = null;
    this.cdr.detectChanges();

    this.vendedorService.getVendedores()
      .pipe(finalize(() => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (response) => {
          this.vendedores = response.sellers;
          this.vendedoresFiltrados = response.sellers;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cargar vendedores:', err);
          this.error = 'No se pudieron cargar los vendedores. Intente de nuevo más tarde.';
          this.cdr.detectChanges();
        }
      });
  }


  mostrarDetalle(item: any) {
    this.selectedItem = item;
    this.dialogVisible = true;
    this.cdr.detectChanges();
  }

  copyToClipboard(id: string): void {
    navigator.clipboard.writeText(id)
      .then(() => {
        this.copiedId = id;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.copiedId = null;
          this.cdr.detectChanges();
        }, 1500);
      })
      .catch(err => console.error('Error al copiar ID: ', err));
  }

  exportarCSV(): void {
    if (this.vendedores.length === 0) {
      this.showToast('No hay vendedores para exportar.', 'error');
      return;
    }

    const headers = {
      id: 'ID',
      full_name: 'Nombre completo',
      doi: 'Documento',
      email: 'Email',
      phone: 'Teléfono',
      zone_description: 'Zona asignada',
      created_at: 'Fecha de creación'
    };

    const data = this.vendedores.map(v => ({
      id: v.id || '',
      full_name: v.full_name,
      doi: v.doi,
      email: v.email,
      phone: v.phone,
      zone_description: v.zone?.description || '',
      created_at: v.created_at || ''
    }));

    this.csvExportService.exportarACsv(data, 'vendedores', headers);
    this.showToast('CSV exportado exitosamente.', 'success');
  }

  crearVendedor(): void {
    this.router.navigate(['/vendedores/crear']);
  }

  onPageSizeChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.pageSize = Number(target.value);
    this.currentPage = 1;
  }

  previousPage(): void {
    if (this.currentPage > 1) this.currentPage--;
  }

  nextPage(): void {
    const totalPages = Math.ceil(this.totalItems / this.pageSize);
    if (this.currentPage < totalPages) this.currentPage++;
  }

  get vendedoresPaginados(): Vendedor[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return this.vendedoresFiltrados.slice(startIndex, endIndex);
  }

  get startItem(): number {
    if (this.totalItems === 0) return 0;
    return (this.currentPage - 1) * this.pageSize + 1;
  }

  get endItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.totalItems);
  }

  get totalItems(): number {
    return this.vendedoresFiltrados.length;
  }

  get hasPreviousPage(): boolean {
    return this.currentPage > 1;
  }

  get hasNextPage(): boolean {
    return this.endItem < this.totalItems;
  }

  showToast(message: string, type: 'success' | 'error'): void {
    this.toastMessage = message;
    this.toastType = type;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.toastMessage = null;
      this.cdr.detectChanges();
    }, 4000);
  }
}
