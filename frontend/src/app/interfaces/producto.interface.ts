export interface ProductCreateRequest {
  name: string;
  details: string;
  store: string;
  batch: string;
  image_url?: string | null;
  due_date: string;
  stock: number;
  price_per_unite: number;
  provider_id: string;
}

export interface ProductCreateResponse {
  id: string;
  name: string;
  details: string;
  store: string;
  batch: string;
  image_url: string | null;
  due_date: string;
  stock: number;
  price_per_unite: number;
  provider_id: string;
  created_at: string;
}

export interface ProductCreateBulkRequest {
  products: ProductCreateRequest[];
}

export interface ProductCreateBulkResponse {
  success: boolean;
  rows_total: number;
  rows_inserted: number;
  errors: number;
  errors_details: string[];
}

export interface ProductRow {
  name: string;
  details: string;
  store: string;
  batch: string;
  image_url?: string;
  due_date: string;
  stock: number | string;
  price_per_unite: number | string;
  provider_id: string;
}
