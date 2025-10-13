import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

interface MenuItem {
  icon: string;
  label: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  menuItems: MenuItem[] = [
    { icon: 'favorite_border', label: 'Inicio', route: '/inicio' },
    { icon: 'directions_car', label: 'Rutas', route: '/rutas' },
    { icon: 'groups', label: 'Vendedores', route: '/vendedores' },
    { icon: 'inventory_2', label: 'Productos', route: '/productos' },
    { icon: 'groups', label: 'Proveedores', route: '/proveedores' },
    { icon: 'folder_open', label: 'Planes de venta', route: '/planes-de-venta' },
    { icon: 'folder_open', label: 'Reportes', route: '/reportes' }
  ];

  isMobileMenuOpen = false;

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  logout(): void {
    console.log('Cerrar sesión');
    // Add your logout logic here
  }
}
