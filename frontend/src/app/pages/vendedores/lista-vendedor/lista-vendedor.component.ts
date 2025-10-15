import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Vendedor } from '../../../interfaces/vendedor.interface';
import { Router } from '@angular/router';


@Component({
  selector: 'app-lista-vendedor',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule
  ],
  templateUrl: './lista-vendedor.component.html',
  styleUrls: ['./lista-vendedor.component.css']
})
export class ListaVendedorComponent {

  constructor(private router: Router) {

  }
  vendedores: Vendedor[] = [
    {
      nombre: 'Tiger Nixon',
      documento: '70216372',
      telefono: '+51 947891236',
      email: 'tiger@gmail.com',
      zonaAsignada: 'Bogotá'
    },
    {
      nombre: 'Sonya Frost',
      documento: '21928475',
      telefono: '+51 947824236',
      email: 'sonya@gmail.com',
      zonaAsignada: 'Bogotá'
    },
    {
      nombre: 'Rhona Davidson',
      documento: '29192933',
      telefono: '+51 947894342',
      email: 'rhona@gmail.com',
      zonaAsignada: 'Bogotá'
    },
    {
      nombre: 'Herrod Chandler',
      documento: '94746123',
      telefono: '+51 230451234',
      email: 'herrod@gmail.com',
      zonaAsignada: 'Bogotá'
    }
  ];

  pageSize = 10;
  currentPage = 1;
  totalItems = 14;

  exportarCSV(): void {
    console.log('Exportar a CSV');
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
