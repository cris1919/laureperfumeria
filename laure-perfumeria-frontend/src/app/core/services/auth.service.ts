import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';

export interface LoginRequest {
  correo: string;
  password: string;
}

export interface LoginResponse {
  mensaje: string;
  token: string;
  tipoToken: string;
  idUsuario: number;
  nombres: string;
  apellidos: string;
  correo: string;
  rol: string;
}

export interface ClientProfile {
  idCliente: number;
  nombres: string;
  apellidos: string;
  correo: string;
  telefono: string | null;
  direccion: string | null;
  estado: string;
  fechaRegistro: string;
}

export interface CheckoutResponse {
  idPedido: number;
  idVenta: number;
  total: number;
}

export interface ClientRegistrationRequest { nombres: string; apellidos: string; correo: string; password: string; telefono?: string; direccion?: string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  readonly currentUser = signal<LoginResponse | null>(null);

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', request);
  }

  registerClient(request: ClientRegistrationRequest): Observable<ClientProfile> {
    return this.http.post<ClientProfile>('/api/clientes', request);
  }

  getClientProfile(idCliente: number): Observable<ClientProfile> {
    return this.http.get<ClientProfile>('/api/clientes/' + idCliente, { headers: this.authorizationHeaders() });
  }

  downloadInvoice(idVenta: number): Observable<Blob> {
    return this.http.get('/api/facturas/venta/' + idVenta + '/pdf', {
      headers: this.authorizationHeaders(), responseType: 'blob'
    });
  }

  downloadAdminExcel(): Observable<Blob> {
    return this.http.get('/api/reportes/excel?agrupacion=dia&limite=10', { headers: this.authorizationHeaders(), responseType: 'blob' });
  }

  getAuthenticated<T>(url: string): Observable<T> {
    return this.http.get<T>(url, { headers: this.authorizationHeaders() });
  }

  postAuthenticated<T>(url: string, body: unknown): Observable<T> {
    return this.http.post<T>(url, body, { headers: this.authorizationHeaders() });
  }

  putAuthenticated<T>(url: string, body: unknown): Observable<T> {
    return this.http.put<T>(url, body, { headers: this.authorizationHeaders() });
  }

  patchAuthenticated<T>(url: string, body: unknown): Observable<T> {
    return this.http.patch<T>(url, body, { headers: this.authorizationHeaders() });
  }

  uploadProductImage(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('archivo', file);
    return this.http.post<{ url: string }>('/api/archivos/productos', formData, { headers: this.authorizationHeaders() });
  }

  private authorizationHeaders(): HttpHeaders {
    if (typeof window === 'undefined') return new HttpHeaders();
    const token = localStorage.getItem('laure_token') || sessionStorage.getItem('laure_token');
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
  }

  saveSession(response: LoginResponse, remember: boolean): void {
    const storage = remember ? localStorage : sessionStorage;
    storage.setItem('laure_token', response.token);
    storage.setItem('laure_user', JSON.stringify(response));
    this.currentUser.set(response);
  }

  loadSession(): void {
    if (typeof window === 'undefined') return;
    const rawUser = localStorage.getItem('laure_user') || sessionStorage.getItem('laure_user');
    if (!rawUser) return;

    try {
      this.currentUser.set(JSON.parse(rawUser) as LoginResponse);
    } catch {
      this.currentUser.set(null);
    }
  }

  clearSession(): void {
    if (typeof window !== 'undefined') {
      localStorage.removeItem('laure_token');
      localStorage.removeItem('laure_user');
      sessionStorage.removeItem('laure_token');
      sessionStorage.removeItem('laure_user');
    }
    this.currentUser.set(null);
  }
}
