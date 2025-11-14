import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Order } from '../../../interfaces/route.interface';
import { RouteService } from '../../../services/rutas/route.service';


interface GroupedOrders {
  [distributionCenterId: number]: {
    centerName: string;
    orders: (Order & { selected: boolean })[];
  };
}

@Component({
  selector: 'app-crear-ruta',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './crear-ruta.component.html',
  styleUrl: './crear-ruta.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CrearRutaComponent implements OnInit {
  groupedOrders = signal<GroupedOrders>({});
  selectedDistributionCenter = signal<number | null>(null);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  loading = signal<boolean>(false);
  showCreateRouteForm = signal<boolean>(false);

  routeForm = signal({
    origin: '',
    origin_lat: 0,
    origin_long: 0,
    destiny: '',
    destiny_lat: 0,
    destiny_long: 0,
    date_limit: '',
    restrictions: '',
    vehicle: ''
  });

  constructor(
    private routeService: RouteService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadOrders();
  }

loadOrders(): void {
  this.loading.set(true);
  this.errorMessage.set('');
  this.groupedOrders.set({}); // ✅ limpiar antes de cargar

  this.routeService.getOrders().subscribe({
    next: async (response: any) => {
      try {
        const receivedOrders = (response.orders || []).filter(
          (order: any) => order.status === 'received'
        );

        if (receivedOrders.length === 0) {
          this.errorMessage.set('No hay órdenes con estado "received".');
          this.loading.set(false);
          return;
        }

        const detailedOrders = await Promise.all(
          receivedOrders.map((order: any) =>
            this.routeService.getOrderById(order.id).toPromise().catch(err => {
              console.warn('Error fetching order details', err);
              return null;
            })
          )
        );

        const validOrders = detailedOrders.filter(o => o !== null);
        if (validOrders.length === 0) {
          this.errorMessage.set('No se pudieron obtener detalles de las órdenes.');
          this.loading.set(false);
          return;
        }

        this.groupOrdersByDistributionCenter(validOrders as Order[]);
        this.loading.set(false);
      } catch (error) {
        console.error('Error fetching detailed orders:', error);
        this.errorMessage.set('Error al obtener detalles de las órdenes.');
        this.loading.set(false);
      }
    },
    error: (error) => {
      console.error('Error loading orders:', error);
      this.errorMessage.set('Error al cargar las órdenes.');
      this.loading.set(false);
    }
  });
}

  groupOrdersByDistributionCenter(orders: Order[]): void {
    const grouped: GroupedOrders = {};

    orders.forEach((order: any) => {
      const centerId = order.distribution_center.id;

      if (!grouped[centerId]) {
        grouped[centerId] = {
          centerName: order.distribution_center.name,
          orders: []
        };
      }

      grouped[centerId].orders.push({
        ...order,
        selected: false
      });
    });

    this.groupedOrders.set(grouped);
  }

  getDistributionCenterKeys(): number[] {
    return Object.keys(this.groupedOrders()).map(key => Number(key));
  }

  selectDistributionCenter(centerId: number): void {
    if (this.selectedDistributionCenter() === centerId) {
      this.selectedDistributionCenter.set(null);
    } else {
      this.selectedDistributionCenter.set(centerId);
      // Deseleccionar todas las órdenes de otros centros
      const grouped = this.groupedOrders();
      this.getDistributionCenterKeys().forEach(key => {
        if (key !== centerId) {
          grouped[key].orders.forEach(order => order.selected = false);
        }
      });
      this.groupedOrders.set({ ...grouped });
    }
  }

  toggleOrderSelection(centerId: number, orderId: string): void {
    const grouped = this.groupedOrders();
    const order = grouped[centerId].orders.find(o => o.id === orderId);
    if (order) {
      order.selected = !order.selected;

      // Si se selecciona una orden, asegurarse de que ese sea el centro seleccionado
      if (order.selected && this.selectedDistributionCenter() !== centerId) {
        this.selectedDistributionCenter.set(centerId);
        // Deseleccionar órdenes de otros centros
        this.getDistributionCenterKeys().forEach(key => {
          if (key !== centerId) {
            grouped[key].orders.forEach(o => o.selected = false);
          }
        });
      }
      this.groupedOrders.set({ ...grouped });
    }
  }

  toggleSelectAll(centerId: number): void {
    const grouped = this.groupedOrders();
    const orders = grouped[centerId].orders;
    const allSelected = orders.every(order => order.selected);

    orders.forEach(order => {
      order.selected = !allSelected;
    });

    if (!allSelected) {
      this.selectedDistributionCenter.set(centerId);
      // Deseleccionar órdenes de otros centros
      this.getDistributionCenterKeys().forEach(key => {
        if (key !== centerId) {
          grouped[key].orders.forEach(o => o.selected = false);
        }
      });
    }
    this.groupedOrders.set({ ...grouped });
  }

getSelectedOrders(): (Order & { selected: boolean })[] {
  const allOrders: (Order & { selected: boolean })[] = [];
  const grouped = this.groupedOrders();

  this.getDistributionCenterKeys().forEach(key => {
    if (grouped[key]?.orders) { // ✅ validación extra
      allOrders.push(...grouped[key].orders.filter(order => order.selected));
    }
  });

  return allOrders;
}

isAllSelected(centerId: number): boolean {
  const orders = this.groupedOrders()[centerId]?.orders || []; // ✅ fallback
  return orders.length > 0 && orders.every(order => order.selected);
}

isSomeSelected(centerId: number): boolean {
  const orders = this.groupedOrders()[centerId]?.orders || []; // ✅ fallback
  return orders.some(order => order.selected) && !this.isAllSelected(centerId);
}

  canCreateRoute(): boolean {
    return this.getSelectedOrders().length > 0;
  }

  openCreateRouteForm(): void {
    if (!this.canCreateRoute()) {
      this.errorMessage.set('Debe seleccionar al menos una orden para crear una ruta.');
      return;
    }

    this.showCreateRouteForm.set(true);
    this.errorMessage.set('');
  }

  cancelCreateRoute(): void {
    this.showCreateRouteForm.set(false);
    this.resetRouteForm();
  }

  resetRouteForm(): void {
    this.routeForm.set({
      origin: '',
      origin_lat: 0,
      origin_long: 0,
      destiny: '',
      destiny_lat: 0,
      destiny_long: 0,
      date_limit: '',
      restrictions: '',
      vehicle: ''
    });
  }

  createRoute(): void {
    if (!this.validateRouteForm()) {
      this.errorMessage.set('Por favor, complete todos los campos requeridos.');
      return;
    }

    const selectedOrders = this.getSelectedOrders();
    const productIds = selectedOrders.flatMap(order =>
      order.products.map(product => product.id)
    );

    const routeData = {
      ...this.routeForm(),
      products: productIds
    };

    this.loading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.routeService.createRoute(routeData).subscribe({
      next: (response) => {
        console.log('Route created:', response);
        this.successMessage.set('Ruta creada exitosamente.');
        this.showCreateRouteForm.set(false);
        this.resetRouteForm();

        // Deseleccionar todas las órdenes
        const grouped = this.groupedOrders();
        this.getDistributionCenterKeys().forEach(key => {
          grouped[key].orders.forEach(order => order.selected = false);
        });
        this.selectedDistributionCenter.set(null);
        this.groupedOrders.set({ ...grouped });

        this.loading.set(false);

        // Navegar al listado después de 2 segundos
        setTimeout(() => {
          this.router.navigate(['/rutas']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error creating route:', error);
        this.errorMessage.set('Error al crear la ruta. Por favor, intente nuevamente.');
        this.loading.set(false);
      }
    });
  }

  validateRouteForm(): boolean {
    const form = this.routeForm();
    return !!(
      form.origin &&
      form.destiny &&
      form.date_limit &&
      form.vehicle &&
      form.origin_lat &&
      form.origin_long &&
      form.destiny_lat &&
      form.destiny_long
    );
  }

  updateFormField(field: string, event: Event): void {
    const input = event.target as HTMLInputElement | HTMLTextAreaElement | null;
    if (!input) return;
    const value = input.value;
    this.routeForm.set({ ...this.routeForm(), [field]: value });
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  }

  getTotalProducts(order: Order): number {
    return order.products.reduce((sum, product) => sum + product.quantity, 0);
  }


}
