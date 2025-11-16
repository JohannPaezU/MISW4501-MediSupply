import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RouteService } from '../../../services/rutas/route.service';
import { RouteModel } from '../../../interfaces/route.interface';

@Component({
  selector: 'app-listado-rutas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './listado-rutas.component.html',
  styleUrl: './listado-rutas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListadoRutasComponent implements OnInit {
  routes = signal<RouteModel[]>([]);
  loading = signal<boolean>(false);
  errorMessage = signal<string>('');
  selectedRouteId = signal<string | null>(null);

  currentPage = signal<number>(1);
  pageSize = signal<number>(10);
  totalRoutes = signal<number>(0);

  constructor(
    private routeService: RouteService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadRoutes();
  }

  loadRoutes(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.routeService.getRoutes().subscribe({
      next: (response) => {
        this.routes.set(response.routes);
        this.totalRoutes.set(response.total_count);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading routes:', error);
        this.errorMessage.set('Error al cargar las rutas. Por favor, intente nuevamente.');
        this.loading.set(false);
      }
    });
  }

  selectRoute(routeId: string): void {
    if (this.selectedRouteId() === routeId) {
      this.selectedRouteId.set(null);
    } else {
      this.selectedRouteId.set(routeId);
    }
  }

  getPaginatedRoutes(): RouteModel[] {
    const start = (this.currentPage() - 1) * this.pageSize();
    const end = start + this.pageSize();
    return this.routes().slice(start, end);
  }

  getTotalPages(): number {
    return Math.ceil(this.totalRoutes() / this.pageSize());
  }

  getEndIndex(): number {
    return Math.min(this.currentPage() * this.pageSize(), this.totalRoutes());
  }

  getStartIndex(): number {
    if (this.totalRoutes() === 0) return 0;
    return (this.currentPage() - 1) * this.pageSize() + 1;
  }

  goToPage(page: number): void {
    if (page >= 1 && page <= this.getTotalPages()) {
      this.currentPage.set(page);
    }
  }

  changePageSize(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(1);
  }

  navigateToMap(): void {
    const selectedId = this.selectedRouteId();
    if (!selectedId) {
      this.errorMessage.set('Debe seleccionar una ruta para ver el mapa.');
      setTimeout(() => this.errorMessage.set(''), 3000);
      return;
    }
    this.router.navigate(['/rutas/rutas-mapa', selectedId]);
  }

  navigateToCreate(): void {
    this.router.navigate(['/rutas/crear-ruta']);
  }

  viewRouteDetails(routeId: string): void {
    this.router.navigate(['/rutas', routeId]);
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  }

  truncateId(id: string): string {
    return id.substring(0, 8) + '...';
  }

  retryLoad(): void {
    this.loadRoutes();
  }
}
