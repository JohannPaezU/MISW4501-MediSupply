import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouteService } from '../../../services/rutas/route.service';
import { OrderInList } from '../../../interfaces/order.interface';
import { GroupedOrders } from '../../../interfaces/route.interface';


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
  selectedDistributionCenter = signal<string | null>(null);
  errorMessage = signal<string>('');
  successMessage = signal<string>('');
  loading = signal<boolean>(false);
  showCreateRouteForm = signal<boolean>(false);
  showSuccessAnimation = signal<boolean>(false);

  routeForm = signal({
    name: '',
    vehicle_plate: '',
    restrictions: ''
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
    this.groupedOrders.set({});

    this.routeService.getAllOrders().subscribe({
      next: (response) => {
        const availableOrders = response.orders.filter(
          order => order.status === 'received' && !order.route
        );

        if (availableOrders.length === 0) {
          this.errorMessage.set('No hay órdenes disponibles para asignar a rutas.');
          this.loading.set(false);
          return;
        }

        this.groupOrdersByDistributionCenter(availableOrders);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error loading orders:', error);
        this.errorMessage.set('Error al cargar las órdenes. Por favor, intente nuevamente.');
        this.loading.set(false);
      }
    });
  }

  navigateBack(): void {
    this.router.navigate(['/rutas']);
  }

  groupOrdersByDistributionCenter(orders: OrderInList[]): void {
    const grouped: GroupedOrders = {};

    orders.forEach(order => {
      const centerId = order.distribution_center.id;

      if (!grouped[centerId]) {
        grouped[centerId] = {
          centerName: order.distribution_center.name,
          centerInfo: `${order.distribution_center.city}, ${order.distribution_center.country}`,
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

  getDistributionCenterKeys(): string[] {
    return Object.keys(this.groupedOrders());
  }

  toggleDistributionCenterExpansion(centerId: string): void {
    if (this.selectedDistributionCenter() === centerId) {
      this.selectedDistributionCenter.set(null);
    } else {
      this.selectedDistributionCenter.set(centerId);
    }
  }

  toggleOrderSelection(centerId: string, orderId: string): void {
    const grouped = this.groupedOrders();
    const order = grouped[centerId].orders.find(o => o.id === orderId);

    if (order) {
      const currentSelectedCenter = this.getSelectedDistributionCenterId();

      if (!order.selected && currentSelectedCenter && currentSelectedCenter !== centerId) {
        this.errorMessage.set('No puede seleccionar órdenes de diferentes centros de distribución.');
        setTimeout(() => this.errorMessage.set(''), 3000);
        return;
      }

      order.selected = !order.selected;
      this.groupedOrders.set({ ...grouped });
    }
  }

  toggleSelectAll(centerId: string): void {
    const grouped = this.groupedOrders();
    const orders = grouped[centerId].orders;
    const allSelected = orders.every(order => order.selected);

    const currentSelectedCenter = this.getSelectedDistributionCenterId();
    if (!allSelected && currentSelectedCenter && currentSelectedCenter !== centerId) {
      this.errorMessage.set('No puede seleccionar órdenes de diferentes centros de distribución.');
      setTimeout(() => this.errorMessage.set(''), 3000);
      return;
    }

    if (allSelected && currentSelectedCenter && currentSelectedCenter !== centerId) {
      this.errorMessage.set('Primero debe deseleccionar las órdenes del otro centro de distribución.');
      setTimeout(() => this.errorMessage.set(''), 3000);
      return;
    }

    orders.forEach(order => {
      order.selected = !allSelected;
    });

    this.groupedOrders.set({ ...grouped });
  }

  getSelectedOrders(): (OrderInList & { selected: boolean })[] {
    const allOrders: (OrderInList & { selected: boolean })[] = [];
    const grouped = this.groupedOrders();

    this.getDistributionCenterKeys().forEach(key => {
      if (grouped[key]?.orders) {
        allOrders.push(...grouped[key].orders.filter(order => order.selected));
      }
    });

    return allOrders;
  }

  getSelectedDistributionCenterId(): string | null {
    const grouped = this.groupedOrders();

    for (const centerId of this.getDistributionCenterKeys()) {
      if (grouped[centerId].orders.some(order => order.selected)) {
        return centerId;
      }
    }

    return null;
  }

  hasMultipleDistributionCentersSelected(): boolean {
    const grouped = this.groupedOrders();
    let centersWithSelection = 0;

    this.getDistributionCenterKeys().forEach(centerId => {
      if (grouped[centerId].orders.some(order => order.selected)) {
        centersWithSelection++;
      }
    });

    return centersWithSelection > 1;
  }

  isAllSelected(centerId: string): boolean {
    const orders = this.groupedOrders()[centerId]?.orders || [];
    return orders.length > 0 && orders.every(order => order.selected);
  }

  isSomeSelected(centerId: string): boolean {
    const orders = this.groupedOrders()[centerId]?.orders || [];
    return orders.some(order => order.selected) && !this.isAllSelected(centerId);
  }

  canCreateRoute(): boolean {
    return this.getSelectedOrders().length > 0 && !this.hasMultipleDistributionCentersSelected();
  }

  openCreateRouteForm(): void {
    if (!this.canCreateRoute()) {
      if (this.hasMultipleDistributionCentersSelected()) {
        this.errorMessage.set('No puede crear una ruta con órdenes de diferentes centros de distribución.');
      } else {
        this.errorMessage.set('Debe seleccionar al menos una orden para crear una ruta.');
      }
      setTimeout(() => this.errorMessage.set(''), 3000);
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
      name: '',
      vehicle_plate: '',
      restrictions: ''
    });
  }

  createRoute(): void {
    if (!this.validateRouteForm()) {
      this.errorMessage.set('Por favor, complete todos los campos requeridos.');
      return;
    }

    const selectedOrders = this.getSelectedOrders();
    const distributionCenterId = this.getSelectedDistributionCenterId();

    if (!distributionCenterId) {
      this.errorMessage.set('Error al identificar el centro de distribución.');
      return;
    }

    const routeData: any = {
      name: this.routeForm().name,
      vehicle_plate: this.routeForm().vehicle_plate,
      restrictions: this.routeForm().restrictions || 'Ninguna',
      distribution_center_id: distributionCenterId,
      order_ids: selectedOrders.map(order => order.id)
    };

    this.loading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.routeService.createRoute(routeData).subscribe({
      next: (response) => {
        console.log('Route created:', response);

        this.showCreateRouteForm.set(false);
        this.resetRouteForm();

        this.showSuccessAnimation.set(true);

        const grouped = this.groupedOrders();
        this.getDistributionCenterKeys().forEach(key => {
          grouped[key].orders.forEach(order => order.selected = false);
        });
        this.groupedOrders.set({ ...grouped });

        this.loading.set(false);

        setTimeout(() => {
          this.showSuccessAnimation.set(false);
          this.router.navigate(['/rutas']);
        }, 2500);
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
    return !!(form.name && form.vehicle_plate);
  }

  updateFormField(field: string, value: any): void {
    const form = this.routeForm();
    this.routeForm.set({ ...form, [field]: value });
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
}
