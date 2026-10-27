import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Categoria } from '../../shared/models/categoria.model';
import { Producto } from '../../shared/models/producto.model';

@Injectable({ providedIn: 'root' })
export class CatalogService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api';

  getFeaturedProducts(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.apiUrl + '/productos/destacados');
  }

  getProducts(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.apiUrl + '/productos');
  }

  getProductById(id: number): Observable<Producto> {
    return this.http.get<Producto>(this.apiUrl + '/productos/' + id);
  }

  searchProducts(name: string): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.apiUrl + '/productos/buscar?nombre=' + encodeURIComponent(name));
  }

  getProductsByCategory(categoryId: number): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.apiUrl + '/productos/categoria/' + categoryId);
  }

  getCategories(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl + '/categorias');
  }
}
