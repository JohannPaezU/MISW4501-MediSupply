import { PlanVenta } from './planVenta.interface';

export interface ReporteData {
  vendedor: string;
  vendedor_id: string;
  producto: string;
  producto_id: string;
  zona: string;
  zona_id: string;
  meta: number;
  ventas: number;
  porcentajeMeta: number;
  periodo: string;
}

export interface FiltrosReporte {
  vendedores: string[];
  zonas: string[];
  productos: string[];
  fechaInicio?: string;
  fechaFin?: string;
  searchTerm?: string;
}

export interface EstadisticasReporte {
  totalVentas: number;
  pedidosPendientes: number;
  cumplimientoPromedio: number;
}
