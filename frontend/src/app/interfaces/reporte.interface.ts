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
  periodo?: string;

  // Campos adicionales opcionales
  cantidad?: number;
  precio_unitario?: number;
  estado_orden?: string;
  comentarios?: string;
}

export interface FiltrosReporte {
  vendedores: string[];
  zonas: string[];
  productos: string[];
  searchTerm: string;
}

export interface EstadisticasReporte {
  totalVentas: number;
  cumplimientoPromedio: number;
}
