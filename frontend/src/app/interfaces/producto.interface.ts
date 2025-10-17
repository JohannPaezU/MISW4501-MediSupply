export interface Producto {
  producto: string;
  lote: string;
  vencimiento: string;
  bodega: string;
}
/**
 * Interfaz que representa la estructura de un producto para la solicitud de creación.
 * Coincide con el schema `ProductCreateRequest` del backend.
 */
export interface ProductCreateRequest {
  name: string;
  details: string;
  store: string;
  batch: string;
  image_url?: string | null;
  due_date: string; // Formato YYYY-MM-DD
  stock: number;
  price_per_unite: number;
  provider_id: string;
}

/**
 * Interfaz que representa la respuesta al crear un producto.
 * Coincide con el schema `ProductCreateResponse` del backend.
 */
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
  created_at: string; // ISO date string
}
