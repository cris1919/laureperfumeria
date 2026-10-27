import { Component, OnInit, inject, signal, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Header } from '../../shared/components/header/header';
import { Footer } from '../../shared/components/footer/footer';
import { CatalogService } from '../../core/services/catalog.service';
import { Producto } from '../../shared/models/producto.model';
import { Categoria } from '../../shared/models/categoria.model';
import { ProductCard } from '../../shared/components/product-card/product-card';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [Header, Footer, RouterLink, ProductCard],
  templateUrl: './products.html',
  styleUrl: './products.css',
})
export class Products implements OnInit {
  private readonly catalog = inject(CatalogService);
  private readonly platformId = inject(PLATFORM_ID);
  readonly products = signal<Producto[]>([]);
  readonly categories = signal<Categoria[]>([]);
  readonly isLoading = signal(true);
  readonly hasError = signal(false);
  readonly filtersOpen = signal(false);

  searchTerm = '';
  selectedCategoryId: number | null = null;
  sortOrder = 'popular';
  currentPage = 1;
  readonly pageSize = 9;

  private readonly fallbackImages = [
    '/assets/images/Imagen1.png',
    '/assets/images/ColeccionDeAutor.png',
    '/assets/images/floral_olfativoa.png',
    '/assets/images/AmaderadoOlfativas.png',
    '/assets/images/CitricoOlfativas.png',
    '/assets/images/OrientalOfativas.png',
    '/assets/images/VainillaOlfativa.png',
  ];

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.isLoading.set(false);
      return;
    }
    this.loadProducts();
    this.catalog.getCategories().subscribe({
      next: (categories) => this.categories.set(categories ?? []),
      error: () => this.hasError.set(true),
    });
  }

  get sortedProducts(): Producto[] {
    const result = [...this.products()];
    if (this.sortOrder === 'price-low') result.sort((a, b) => a.precioVenta - b.precioVenta);
    if (this.sortOrder === 'price-high') result.sort((a, b) => b.precioVenta - a.precioVenta);
    if (this.sortOrder === 'name') result.sort((a, b) => a.nombre.localeCompare(b.nombre));
    return result;
  }

  get visibleProducts(): Producto[] {
    const start = (this.currentPage - 1) * this.pageSize;
    return this.sortedProducts.slice(start, start + this.pageSize);
  }

  get pageCount(): number {
    return Math.max(1, Math.ceil(this.sortedProducts.length / this.pageSize));
  }

  get resultLabel(): string {
    return this.products().length + ' fragancias';
  }

  search(): void {
    const term = this.searchTerm.trim();
    this.currentPage = 1;
    this.isLoading.set(true);
    this.hasError.set(false);
    const request = term
      ? this.catalog.searchProducts(term)
      : this.selectedCategoryId
        ? this.catalog.getProductsByCategory(this.selectedCategoryId)
        : this.catalog.getProducts();
    request.subscribe({
      next: (products) => {
        this.setAvailableProducts(products);
        this.isLoading.set(false);
      },
      error: () => {
        this.products.set([]);
        this.hasError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  selectCategory(categoryId: number | null): void {
    this.selectedCategoryId = categoryId;
    this.currentPage = 1;
    this.searchTerm = '';
    this.search();
  }

  changeSort(event: Event): void {
    this.sortOrder = (event.target as HTMLSelectElement).value;
    this.currentPage = 1;
  }

  goToPage(page: number): void {
    this.currentPage = Math.min(Math.max(page, 1), this.pageCount);
  }

  productImage(product: Producto, index: number): string {
    return product.imagenUrl || this.fallbackImages[index % this.fallbackImages.length];
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 2 }).format(price || 0);
  }

  productDescription(product: Producto): string {
    return product.descripcion || 'Una fragancia seleccionada por Laure para una presencia inolvidable.';
  }

  loadProducts(): void {
    this.isLoading.set(true);
    this.catalog.getProducts().subscribe({
      next: (products) => {
        this.setAvailableProducts(products);
        this.isLoading.set(false);
      },
      error: () => {
        this.hasError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  private setAvailableProducts(products: Producto[] | null | undefined): void {
    this.products.set((products ?? []).filter((product) => Number(product.stock) > 0));
  }
}
