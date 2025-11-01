export interface Vendedor {
  id?: string;
  full_name: string;
  doi: string;
  email: string;
  phone: string;
  created_at?: string;
  zone?: ZonaGeografica;
}

export interface VendedorResponse {
  total_count: number;
  sellers: Vendedor[];
}

export interface VendedorDetailResponse extends Vendedor { }

export interface CreateVendedorRequest {
  full_name: string;
  doi: string;
  email: string;
  phone: string;
  zone_id: string;
}

export interface CreateVendedorResponse extends Vendedor { }

export interface ZonaGeografica {
  id: string;
  description: string;
}

export interface ZonesResponse {
  total_count: number;
  zones: ZonaGeografica[];
}

// Mantener Zone como alias para compatibilidad
export type Zone = ZonaGeografica;
