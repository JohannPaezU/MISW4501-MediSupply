export interface LoginResponse {
  message: string;
  otp_expiration_minutes?: number;
}

export interface VerifyOtpResponse {
  message: string;
  access_token?: string;
  token_type?: string;
}
