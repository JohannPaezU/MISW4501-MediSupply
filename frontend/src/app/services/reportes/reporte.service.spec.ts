import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReporteService } from './reportes.service';
import { environment } from '../../../environments/environment';
import { ReporteData } from '../../interfaces/reporte.interface';

describe('ReporteService', () => {
  let service: ReporteService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/reports/orders`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReporteService]
    });

    service = TestBed.inject(ReporteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should transform orders with complete data correctly', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-1',
          seller: {
            id: 'seller-1',
            full_name: 'Juan Pérez',
            zone: {
              id: 'zone-1',
              description: 'Zona Norte'
            }
          },
          products: [
            {
              id: 'prod-1',
              name: 'Producto A',
              price_per_unit: 100,
              quantity: 5
            }
          ],
          delivery_date: '2025-01-15',
          status: 'completed',
          comments: 'Entrega exitosa'
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(1);

      const reporte = reportes[0];
      expect(reporte.id).toBe('order-1_prod-1');
      expect(reporte.vendedor).toBe('Juan Pérez');
      expect(reporte.vendedor_id).toBe('seller-1');
      expect(reporte.producto).toBe('Producto A');
      expect(reporte.producto_id).toBe('prod-1');
      expect(reporte.zona).toBe('Zona Norte');
      expect(reporte.zona_id).toBe('zone-1');
      expect(reporte.ventas).toBe(500); // 100 * 5
      expect(reporte.meta).toBe(0);
      expect(reporte.porcentajeMeta).toBe(0);
      expect(reporte.periodo).toBe('2025-01-15');
      expect(reporte.cantidad).toBe(5);
      expect(reporte.precio_unitario).toBe(100);
      expect(reporte.estado_orden).toBe('completed');
      expect(reporte.comentarios).toBe('Entrega exitosa');

      done();
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir el branch cuando seller es null/undefined
  it('should handle missing seller data with fallback values', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-2',
          seller: null,
          products: [
            {
              id: 'prod-2',
              name: 'Producto B',
              price_per_unit: 50,
              quantity: 2
            }
          ],
          delivery_date: '2025-02-01',
          status: 'pending',
          comments: ''
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(1);

      const reporte = reportes[0];
      expect(reporte.vendedor).toBe('Sin vendedor');
      expect(reporte.vendedor_id).toBe('');
      expect(reporte.zona).toBe('Sin zona');
      expect(reporte.zona_id).toBe('');

      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir el branch cuando seller.zone es null/undefined
  it('should handle missing zone data with fallback values', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-3',
          seller: {
            id: 'seller-2',
            full_name: 'Ana García',
            zone: null
          },
          products: [
            {
              id: 'prod-3',
              name: 'Producto C',
              price_per_unit: 75,
              quantity: 3
            }
          ],
          delivery_date: '2025-03-10',
          status: 'shipped',
          comments: 'En camino'
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(1);

      const reporte = reportes[0];
      expect(reporte.vendedor).toBe('Ana García');
      expect(reporte.vendedor_id).toBe('seller-2');
      expect(reporte.zona).toBe('Sin zona');
      expect(reporte.zona_id).toBe('');

      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir el branch cuando product.name es null/undefined
  it('should handle missing product name with fallback value', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-4',
          seller: {
            id: 'seller-3',
            full_name: 'Carlos López',
            zone: {
              id: 'zone-2',
              description: 'Zona Sur'
            }
          },
          products: [
            {
              id: 'prod-4',
              name: null,
              price_per_unit: 200,
              quantity: 1
            }
          ],
          delivery_date: '2025-04-20',
          status: 'completed',
          comments: ''
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(1);

      const reporte = reportes[0];
      expect(reporte.producto).toBe('Sin producto');
      expect(reporte.producto_id).toBe('prod-4');

      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir branches cuando price_per_unit o quantity son 0/null
  it('should handle zero or missing price and quantity', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-5',
          seller: {
            id: 'seller-4',
            full_name: 'María Rodríguez',
            zone: {
              id: 'zone-3',
              description: 'Zona Este'
            }
          },
          products: [
            {
              id: 'prod-5',
              name: 'Producto D',
              price_per_unit: 0,
              quantity: 0
            },
            {
              id: 'prod-6',
              name: 'Producto E',
              price_per_unit: null,
              quantity: null
            }
          ],
          delivery_date: '2025-05-15',
          status: 'cancelled',
          comments: 'Cancelado por cliente'
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(2);

      // Primer producto con valores en 0
      expect(reportes[0].ventas).toBe(0);
      expect(reportes[0].cantidad).toBe(0);
      expect(reportes[0].precio_unitario).toBe(0);

      // Segundo producto con valores null
      expect(reportes[1].ventas).toBe(0);
      expect(reportes[1].cantidad).toBe(0);
      expect(reportes[1].precio_unitario).toBe(0);

      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir el branch cuando delivery_date es null/undefined
  it('should handle missing delivery_date with empty string', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-6',
          seller: {
            id: 'seller-5',
            full_name: 'Pedro Martínez',
            zone: {
              id: 'zone-4',
              description: 'Zona Oeste'
            }
          },
          products: [
            {
              id: 'prod-7',
              name: 'Producto F',
              price_per_unit: 150,
              quantity: 4
            }
          ],
          delivery_date: null,
          status: 'pending',
          comments: ''
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(1);

      const reporte = reportes[0];
      expect(reporte.periodo).toBe('');

      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir branches cuando status o comments son undefined
  it('should handle missing status and comments with empty strings', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-7',
          seller: {
            id: 'seller-6',
            full_name: 'Laura González',
            zone: {
              id: 'zone-5',
              description: 'Zona Centro'
            }
          },
          products: [
            {
              id: 'prod-8',
              name: 'Producto G',
              price_per_unit: 80,
              quantity: 6
            }
          ],
          delivery_date: '2025-06-01',
          status: null,
          comments: null
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(1);

      const reporte = reportes[0];
      expect(reporte.estado_orden).toBe('');
      expect(reporte.comentarios).toBe('');

      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir múltiples productos en una orden
  it('should handle multiple products in a single order', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-8',
          seller: {
            id: 'seller-7',
            full_name: 'Roberto Sánchez',
            zone: {
              id: 'zone-6',
              description: 'Zona Industrial'
            }
          },
          products: [
            {
              id: 'prod-9',
              name: 'Producto H',
              price_per_unit: 100,
              quantity: 2
            },
            {
              id: 'prod-10',
              name: 'Producto I',
              price_per_unit: 50,
              quantity: 10
            },
            {
              id: 'prod-11',
              name: 'Producto J',
              price_per_unit: 25,
              quantity: 8
            }
          ],
          delivery_date: '2025-07-10',
          status: 'completed',
          comments: 'Todo en orden'
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(3);

      expect(reportes[0].id).toBe('order-8_prod-9');
      expect(reportes[0].ventas).toBe(200);

      expect(reportes[1].id).toBe('order-8_prod-10');
      expect(reportes[1].ventas).toBe(500);

      expect(reportes[2].id).toBe('order-8_prod-11');
      expect(reportes[2].ventas).toBe(200);

      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir órdenes vacías
  it('should return empty array when orders array is empty', (done) => {
    const mockResponse = {
      orders: []
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(0);
      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para cubrir orden sin productos
  it('should handle order with empty products array', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-9',
          seller: {
            id: 'seller-8',
            full_name: 'Diana Torres',
            zone: {
              id: 'zone-7',
              description: 'Zona Residencial'
            }
          },
          products: [],
          delivery_date: '2025-08-05',
          status: 'pending',
          comments: 'Sin productos'
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(0);
      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });

  // ✅ Test para verificar cálculo correcto de ventas con decimales
  it('should calculate sales correctly with decimal values', (done) => {
    const mockResponse = {
      orders: [
        {
          id: 'order-10',
          seller: {
            id: 'seller-9',
            full_name: 'Fernando Castro',
            zone: {
              id: 'zone-8',
              description: 'Zona Comercial'
            }
          },
          products: [
            {
              id: 'prod-12',
              name: 'Producto K',
              price_per_unit: 15.75,
              quantity: 7
            }
          ],
          delivery_date: '2025-09-12',
          status: 'completed',
          comments: 'Pago verificado'
        }
      ]
    };

    service.getReportes().subscribe((reportes: ReporteData[]) => {
      expect(reportes.length).toBe(1);
      expect(reportes[0].ventas).toBe(110.25); // 15.75 * 7
      done();
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush(mockResponse);
  });
});
