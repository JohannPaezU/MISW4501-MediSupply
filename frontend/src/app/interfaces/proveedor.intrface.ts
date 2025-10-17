/**
 * Interfaz base que representa la estructura de un proveedor recibido desde el backend.
 * Coincide con el schema `ProviderBase` del backend.
 */
export interface ProviderBase {
  id: string;
  name: string;
  rit: string;
  city: string;
  country: string;
  image_url: string | null;
  email: string;
  phone: string;
  created_at: string; // ISO date string
}

/**
 * Interfaz para la respuesta del endpoint que obtiene todos los proveedores.
 * Coincide con el schema `GetProvidersResponse` del backend.
 */
export interface GetProvidersResponse {
  total_count: number;
  providers: ProviderBase[];
}


/**
 * Interfaz que representa la estructura de un proveedor para la solicitud de creación.
 * Coincide con el schema `ProviderCreateRequest` del backend.
 */
export interface ProviderCreateRequest {
  name: string;
  rit: string;
  city: string;
  country: string;
  image_url?: string | null;
  email: string;
  phone: string;
}

/**
 * Interfaz que representa la respuesta al crear un proveedor.
 * Hereda de ProviderBase ya que la respuesta contiene todos los campos.
 */
export interface ProviderCreateResponse extends ProviderBase {}

