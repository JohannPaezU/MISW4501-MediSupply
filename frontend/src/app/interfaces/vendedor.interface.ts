
export interface Vendedor {
  id?: number;
  fullname: string;
  document: string;
  email: string;
  phone: string;
  zone_id: number;
  zone_description?: string;
  created_at?: string;
}

export interface VendedorResponse {
  sellers: Vendedor[];
}

export interface VendedorDetailResponse extends Vendedor { }

export interface CreateVendedorRequest {
  fullname: string;
  document: string;
  email: string;
  phone: string;
  zone_id: number;
}

export interface CreateVendedorResponse extends Vendedor { }

export interface Zone {
  id: number;
  description: string;
}

export interface ZonesResponse {
  zones: Zone[];
}
