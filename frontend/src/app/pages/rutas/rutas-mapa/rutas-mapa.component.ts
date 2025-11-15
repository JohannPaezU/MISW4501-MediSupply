import { ChangeDetectionStrategy, Component, OnInit, signal, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { RouteService } from '../../../services/rutas/route.service';
import { RouteMapResponse } from '../../../interfaces/route.interface';
import { GoogleMapsLoaderService } from '../../../services/google/googleMapsLoader.service';

declare const google: any;

@Component({
  selector: 'app-rutas-mapa',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rutas-mapa.component.html',
  styleUrl: './rutas-mapa.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RutasMapaComponent implements OnInit, AfterViewInit, OnDestroy {
  routeData = signal<RouteMapResponse | null>(null);
  loading = signal<boolean>(false);
  errorMessage = signal<string>('');
  lastUpdate = signal<string>('');
  map: any = null;
  markers: any[] = [];
  routeId: string | null = null;
  private mapInitialized = false;

  constructor(
    private routeService: RouteService,
    private router: Router,
    private route: ActivatedRoute,
    private googleMapsLoader: GoogleMapsLoaderService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.routeId = params.get('id');
      if (this.routeId) {
        this.loadRouteMap();
      } else {
        this.errorMessage.set('No se especificó una ruta para mostrar.');
      }
    });
    this.updateLastUpdateTime();
  }

  ngAfterViewInit(): void {

    this.loading.set(true);
    this.googleMapsLoader.loadGoogleMaps()
      .then(() => {
        this.loading.set(false);
        if (this.routeData()) {
          this.initMap();
        }
      })
      .catch((error) => {
        console.error('Error loading Google Maps:', error);
        this.errorMessage.set('Error al cargar Google Maps. Por favor, recargue la página.');
        this.loading.set(false);
      });
  }

  ngOnDestroy(): void {

    this.clearMarkers();
    this.map = null;
  }

  loadRouteMap(): void {
    if (!this.routeId) return;

    this.loading.set(true);
    this.errorMessage.set('');

    this.routeService.getRouteMap(this.routeId).subscribe({
      next: (response) => {
        this.routeData.set(response);
        this.loading.set(false);


        if (this.googleMapsLoader.isGoogleMapsLoaded() && !this.mapInitialized) {
          this.initMap();
        }
      },
      error: (error) => {
        console.error('Error loading route map:', error);
        this.errorMessage.set('Error al cargar el mapa de la ruta. Por favor, intente nuevamente.');
        this.loading.set(false);
      }
    });
  }

  initMap(): void {
    const data = this.routeData();
    if (!data || data.stops.length === 0) {
      this.errorMessage.set('No hay paradas disponibles para mostrar en el mapa.');
      return;
    }

    if (this.mapInitialized) {
      this.addMarkersAndRoute();
      return;
    }

    const centerLat = data.stops[0].latitude;
    const centerLng = data.stops[0].longitude;

    const mapOptions = {
      center: { lat: centerLat, lng: centerLng },
      zoom: 13,
      mapTypeId: 'roadmap',
      gestureHandling: 'greedy',
      disableDefaultUI: false,
      zoomControl: true,
      mapTypeControl: false,
      scaleControl: false,
      streetViewControl: false,
      rotateControl: false,
      fullscreenControl: true
    };

    const mapElement = document.getElementById('map');
    if (mapElement && typeof google !== 'undefined') {
      this.map = new google.maps.Map(mapElement, mapOptions);
      this.mapInitialized = true;
      this.addMarkersAndRoute();
    } else {
      this.errorMessage.set('Google Maps no está disponible.');
    }
  }

  clearMarkers(): void {
    this.markers.forEach(marker => {
      if (marker.setMap) {
        marker.setMap(null);
      }
    });
    this.markers = [];
  }

  addMarkersAndRoute(): void {
    if (!this.map) return;

    const data = this.routeData();
    if (!data) return;


    this.clearMarkers();

    const bounds = new google.maps.LatLngBounds();
    const routePath: any[] = [];

    data.stops.forEach((stop, index) => {
      const position = { lat: stop.latitude, lng: stop.longitude };

      const marker = new google.maps.Marker({
        position: position,
        map: this.map,
        title: stop.client_name,
        label: {
          text: (index + 1).toString(),
          color: 'white',
          fontWeight: 'bold'
        },
        icon: {
          path: google.maps.SymbolPath.CIRCLE,
          scale: 12,
          fillColor: this.getStopColor(index),
          fillOpacity: 1,
          strokeColor: 'white',
          strokeWeight: 2
        },
        optimized: true
      });

      const infoWindow = new google.maps.InfoWindow({
        content: `
          <div style="padding: 10px; max-width: 250px;">
            <h3 style="margin: 0 0 8px 0; font-size: 14px; font-weight: 600;">
              Parada ${index + 1}
            </h3>
            <p style="margin: 4px 0; font-size: 12px;">
              <strong>Cliente:</strong> ${stop.client_name}
            </p>
            <p style="margin: 4px 0; font-size: 12px;">
              <strong>Dirección:</strong> ${stop.client_address}
            </p>
            <p style="margin: 4px 0; font-size: 12px;">
              <strong>Teléfono:</strong> ${stop.client_phone}
            </p>
            <p style="margin: 4px 0; font-size: 12px;">
              <strong>Fecha entrega:</strong> ${this.formatDate(stop.delivery_date)}
            </p>
            <p style="margin: 4px 0; font-size: 12px;">
              <strong>Estado:</strong>
              <span style="color: #1565c0; font-weight: 500;">${stop.order_status}</span>
            </p>
          </div>
        `
      });

      marker.addListener('click', () => {
        this.markers.forEach(m => {
          if (m.infoWindow && m.infoWindow !== infoWindow) {
            m.infoWindow.close();
          }
        });
        infoWindow.open(this.map, marker);
      });

      (marker as any).infoWindow = infoWindow;

      this.markers.push(marker);
      bounds.extend(position);
      routePath.push(position);
    });

    if (routePath.length > 1) {
      const routeLine = new google.maps.Polyline({
        path: routePath,
        geodesic: true,
        strokeColor: '#2196F3',
        strokeOpacity: 0.8,
        strokeWeight: 4,
        map: this.map
      });


      const lineSymbol = {
        path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW,
        strokeColor: '#1565C0',
        fillColor: '#1565C0',
        fillOpacity: 1,
        scale: 3
      };

      routeLine.setOptions({
        icons: [{
          icon: lineSymbol,
          offset: '50%',
          repeat: '100px'
        }]
      });

      this.markers.push(routeLine);
    }


    if (data.stops.length > 0) {
      this.map.fitBounds(bounds);
    }
  }

  getStopColor(index: number): string {
    const colors = [
      '#4CAF50',
      '#FF9800',
      '#9C27B0',
      '#F44336',
      '#2196F3',
      '#FFEB3B',
    ];
    return colors[index % colors.length];
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
    if (!dateString) return 'N/A';
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
    if (this.routeId) {
      this.loadRouteMap();
      this.updateLastUpdateTime();
    }
  }
}
