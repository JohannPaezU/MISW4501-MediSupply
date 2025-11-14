import { ChangeDetectionStrategy, Component, OnInit, signal, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RouteService } from '../../../services/rutas/route.service';
import { RouteModel } from '../../../interfaces/route.interface';


declare const google: any;

@Component({
  selector: 'app-rutas-mapa',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rutas-mapa.component.html',
  styleUrl: './rutas-mapa.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RutasMapaComponent implements OnInit, AfterViewInit {
  routes = signal<RouteModel[]>([]);
  loading = signal<boolean>(false);
  errorMessage = signal<string>('');
  map: any = null;
  markers: any[] = [];
  lastUpdate = signal<string>('');

  constructor(
    private routeService: RouteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRoutes();
    this.updateLastUpdateTime();
  }

  ngAfterViewInit(): void {
    this.initMap();
  }

  loadRoutes(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.routeService.getRoutes().subscribe({
      next: (response) => {
        this.routes.set(response.routes);
        this.loading.set(false);
        if (this.map) {
          this.addMarkersToMap();
        }
      },
      error: (error) => {
        console.error('Error loading routes:', error);
        this.errorMessage.set('Error al cargar las rutas. Por favor, intente nuevamente.');
        this.loading.set(false);
      }
    });
  }

  initMap(): void {
    // Configuración inicial del mapa (centrado en una ubicación por defecto)
    const mapOptions = {
      center: { lat: 6.2476, lng: -75.5658 }, // Medellín por defecto
      zoom: 12,
      mapTypeId: 'roadmap'
    };

    const mapElement = document.getElementById('map');
    if (mapElement && typeof google !== 'undefined') {
      this.map = new google.maps.Map(mapElement, mapOptions);
      if (this.routes().length > 0) {
        this.addMarkersToMap();
      }
    } else {
      // Si Google Maps no está cargado, mostrar mapa estático o mensaje
      console.warn('Google Maps not loaded');
    }
  }

  addMarkersToMap(): void {
    if (!this.map) return;

    // Limpiar marcadores existentes
    this.markers.forEach(marker => marker.setMap(null));
    this.markers = [];

    const bounds = new google.maps.LatLngBounds();

    this.routes().forEach((route, index) => {
      // Marcador de origen
      const originMarker = new google.maps.Marker({
        position: { lat: route.origin_lat, lng: route.origin_long },
        map: this.map,
        title: `Origen: ${route.origin}`,
        label: {
          text: 'O',
          color: 'white',
          fontWeight: 'bold'
        },
        icon: {
          path: google.maps.SymbolPath.CIRCLE,
          scale: 10,
          fillColor: '#4CAF50',
          fillOpacity: 1,
          strokeColor: 'white',
          strokeWeight: 2
        }
      });

      // Marcador de destino
      const destinyMarker = new google.maps.Marker({
        position: { lat: route.destiny_lat, lng: route.destiny_long },
        map: this.map,
        title: `Destino: ${route.destiny}`,
        label: {
          text: 'D',
          color: 'white',
          fontWeight: 'bold'
        },
        icon: {
          path: google.maps.SymbolPath.CIRCLE,
          scale: 10,
          fillColor: '#F44336',
          fillOpacity: 1,
          strokeColor: 'white',
          strokeWeight: 2
        }
      });

      // Info windows
      const originInfoWindow = new google.maps.InfoWindow({
        content: `
          <div style="padding: 10px;">
            <h3 style="margin: 0 0 8px 0; font-size: 14px; font-weight: 600;">Origen</h3>
            <p style="margin: 4px 0; font-size: 12px;"><strong>Ubicación:</strong> ${route.origin}</p>
            <p style="margin: 4px 0; font-size: 12px;"><strong>Vehículo:</strong> ${route.vehicle}</p>
            <p style="margin: 4px 0; font-size: 12px;"><strong>Ruta ID:</strong> ${route.id}</p>
          </div>
        `
      });

      const destinyInfoWindow = new google.maps.InfoWindow({
        content: `
          <div style="padding: 10px;">
            <h3 style="margin: 0 0 8px 0; font-size: 14px; font-weight: 600;">Destino</h3>
            <p style="margin: 4px 0; font-size: 12px;"><strong>Ubicación:</strong> ${route.destiny}</p>
            <p style="margin: 4px 0; font-size: 12px;"><strong>Fecha Límite:</strong> ${this.formatDate(route.date_limit)}</p>
            <p style="margin: 4px 0; font-size: 12px;"><strong>Ruta ID:</strong> ${route.id}</p>
          </div>
        `
      });

      originMarker.addListener('click', () => {
        originInfoWindow.open(this.map, originMarker);
      });

      destinyMarker.addListener('click', () => {
        destinyInfoWindow.open(this.map, destinyMarker);
      });

      // Línea de ruta
      const routeLine = new google.maps.Polyline({
        path: [
          { lat: route.origin_lat, lng: route.origin_long },
          { lat: route.destiny_lat, lng: route.destiny_long }
        ],
        geodesic: true,
        strokeColor: '#2196F3',
        strokeOpacity: 0.8,
        strokeWeight: 3,
        map: this.map
      });

      this.markers.push(originMarker, destinyMarker, routeLine);

      // Expandir bounds
      bounds.extend({ lat: route.origin_lat, lng: route.origin_long });
      bounds.extend({ lat: route.destiny_lat, lng: route.destiny_long });
    });

    // Ajustar el mapa para mostrar todos los marcadores
    if (this.routes().length > 0) {
      this.map.fitBounds(bounds);
    }
  }

  updateLastUpdateTime(): void {
    const now = new Date();
    const formatted = now.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
    this.lastUpdate.set(`Última actualización: ${formatted}`);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  }

  navigateBack(): void {
    this.router.navigate(['/rutas']);
  }

  retryLoad(): void {
    this.loadRoutes();
    this.updateLastUpdateTime();
  }
}
