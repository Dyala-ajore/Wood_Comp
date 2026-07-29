export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  name: string;
  email: string;
  role: string;
}

export interface ApiErrorResponse {
  message?: string;
  [fieldName: string]: string | undefined;
}
