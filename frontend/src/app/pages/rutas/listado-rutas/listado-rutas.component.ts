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

  // Paginación
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
        this.totalRoutes.set(response.routes.length);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading routes:', error);
        this.errorMessage.set('Error al cargar las rutas. Por favor, intente nuevamente.');
        this.loading.set(false);
      }
    });
  }

  getPaginatedRoutes(): RouteModel[] {
    const start = (this.currentPage() - 1) * this.pageSize();
    const end = start + this.pageSize();
    return this.routes().slice(start, end);
  }

  getTotalPages(): number {
    return Math.ceil(this.totalRoutes() / this.pageSize());
  }

  // Método para calcular el índice final de la página actual
  getEndIndex(): number {
    return Math.min(this.currentPage() * this.pageSize(), this.totalRoutes());
  }

  // Método para calcular el índice inicial de la página actual
  getStartIndex(): number {
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
    this.router.navigate(['/rutas/mapa']);
  }

  navigateToCreate(): void {
    this.router.navigate(['/rutas/crear-ruta']);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  }

  retryLoad(): void {
    this.loadRoutes();
  }
}
