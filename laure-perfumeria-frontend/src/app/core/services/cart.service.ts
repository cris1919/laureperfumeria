import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { CheckoutResponse } from './auth.service';
import { Producto } from '../../shared/models/producto.model';

export interface CartItem {
  product: Producto;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http = inject(HttpClient);
  readonly items = signal<CartItem[]>([]);
  readonly totalCount = computed(() => this.items().reduce((total, item) => total + item.quantity, 0));
  readonly total = computed(() => this.items().reduce((total, item) => total + (item.product.precioVenta || 0) * item.quantity, 0));

  constructor() {
    this.load();
  }

  add(product: Producto, quantity = 1): void {
    const items = [...this.items()];
    const existing = items.find((item) => item.product.idProducto === product.idProducto);
    if (existing) existing.quantity += quantity;
    else items.push({ product, quantity });
    this.items.set(items);
    this.persist();
  }

  updateQuantity(idProducto: number, quantity: number): void {
    const items = this.items()
      .map((item) => item.product.idProducto === idProducto
        ? { ...item, quantity: Math.min(quantity, Math.max(item.product.stock, 0)) }
        : item)
      .filter((item) => item.quantity > 0);
    this.items.set(items);
    this.persist();
  }

  remove(idProducto: number): void {
    this.items.set(this.items().filter((item) => item.product.idProducto !== idProducto));
    this.persist();
  }

  clear(): void {
    this.items.set([]);
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      localStorage.removeItem('laure_cart');
      if (typeof sessionStorage !== 'undefined') sessionStorage.removeItem('laure_cart');
    }
  }

  checkout(idCliente: number): Observable<CheckoutResponse> {
    const token = typeof window !== 'undefined' ? localStorage.getItem('laure_token') || sessionStorage.getItem('laure_token') : null;
    const headers = token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
    return this.http.post<CheckoutResponse>('/api/checkout', {
      idCliente, costoEnvio: 0, observaciones: 'Compra realizada desde la tienda online',
      detalles: this.items().map((item) => ({ idProducto: item.product.idProducto, cantidad: item.quantity })),
    }, { headers });
  }

  private load(): void {
    if (typeof window === 'undefined' || typeof localStorage === 'undefined') return;
    const raw = localStorage.getItem('laure_cart');
    if (!raw) return;
    try {
      const stored = JSON.parse(raw) as CartItem[];
      const items = stored
        .map((item) => ({
          ...item,
          quantity: Math.min(item.quantity, Math.max(item.product.stock, 0)),
        }))
        .filter((item) => item.quantity > 0);
      this.items.set(items);
      this.persist();
    } catch { this.items.set([]); }
  }

  private persist(): void {
    if (typeof window !== 'undefined' && typeof localStorage !== 'undefined') {
      const items = this.items();
      if (items.length) localStorage.setItem('laure_cart', JSON.stringify(items));
      else {
        localStorage.removeItem('laure_cart');
        if (typeof sessionStorage !== 'undefined') sessionStorage.removeItem('laure_cart');
      }
    }
  }
}
