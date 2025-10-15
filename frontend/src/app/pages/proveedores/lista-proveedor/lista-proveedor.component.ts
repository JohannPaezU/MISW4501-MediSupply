import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router } from '@angular/router';
import { Proveedor } from '../../../interfaces/proveedor.intrface';

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

export class ListaProveedorComponent {
  constructor(private router: Router) {}

  proveedores: Proveedor[] = [
    {
      nombre: 'Miguel Padilla',
      numeroRIT: '1010101011',
      ciudad: 'Bogotá',
      correo: 'miguel@gmail.com',
      telefono: '+51947891236',
      contacto: 'Juan Peréz'
    },
    {
      nombre: 'Juan Cervantes',
      numeroRIT: '2020202021',
      ciudad: 'Bogotá',
      correo: 'juan@gmail.com',
      telefono: '+51947231236',
      contacto: 'Lolo López'
    },
    {
      nombre: 'Johann Páez',
      numeroRIT: '3030303031',
      ciudad: 'Bogotá',
      correo: 'johann@gmail.com',
      telefono: '+51927361326',
      contacto: 'Tito Ramírez'
    }
  ];

  pageSize = 10;
  currentPage = 1;
  totalItems = 14;

  exportarCSV(): void {
    console.log('Exportar a CSV');
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

  get startItem(): number {
    return (this.currentPage - 1) * this.pageSize + 1;
  }

  get endItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.totalItems);
  }

}
