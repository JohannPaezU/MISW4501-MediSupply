import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MenuItem } from '../../interfaces/menuItem.interface';
import { AuthService } from '../../services/login/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, MatIconModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})

export class SidebarComponent {
  constructor(private router: Router, private authService: AuthService) { }

  menuItems: MenuItem[] = [
    { icon: 'home', label: 'Inicio', route: '/home' },
    { icon: 'map', label: 'Rutas', route: '/rutas' },
    {
      icon: 'storefront',
      label: 'Vendedores',
      route: '/vendedores',
      activeFor: ['/vendedores', '/vendedores/crear']
    },
    {
      icon: 'inventory',
      label: 'Productos',
      route: '/productos',
      activeFor: ['/productos', '/productos/crear', '/productos/crear-masivo']
    },
    {
      icon: 'local_shipping',
      label: 'Proveedores',
      route: '/proveedores',
      activeFor: ['/proveedores', '/proveedores/crear']
    },
    {
      icon: 'trending_up',
      label: 'Planes de venta',
      route: '/planes-de-venta',
      activeFor: ['/planes-de-venta', '/planes-de-venta/crear']
    },
    {
      icon: 'insights',
      label: 'Reportes',
      route: '/reportes'
    }
  ];

  isMobileMenuOpen = false;

  isActive(item: MenuItem): boolean {
    const currentUrl = this.router.url;
    if (item.activeFor) {
      return item.activeFor.some(route => currentUrl.startsWith(route));
    }
    return currentUrl === item.route;
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  logout(): void {
    this.authService.logout();
  }
}
