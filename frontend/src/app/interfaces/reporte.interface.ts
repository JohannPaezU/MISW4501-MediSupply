import { PlanVenta } from './planVenta.interface';

export interface ReporteData {
  id?: string;
  vendedor: string;
  vendedor_id: string;
  producto: string;
  producto_id: string;
  zona: string;
  zona_id: string;
  ventas: number;
  meta: number;
  porcentajeMeta: number;
  periodo: string;
  status?: string;
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
  cumplimientoPromedio: number;
}
