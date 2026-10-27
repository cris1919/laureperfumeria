import { isPlatformBrowser } from '@angular/common';
import { Component, OnInit, PLATFORM_ID, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CatalogService } from '../../core/services/catalog.service';
import { CartService } from '../../core/services/cart.service';
import { Header } from '../../shared/components/header/header';
import { Footer } from '../../shared/components/footer/footer';
import { ProductCard } from '../../shared/components/product-card/product-card';
import { Producto } from '../../shared/models/producto.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [Header, Footer, RouterLink, ProductCard],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css',
})
export class ProductDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly catalog = inject(CatalogService);
  private readonly cart = inject(CartService);
  private readonly platformId = inject(PLATFORM_ID);

  readonly product = signal<Producto | null>(null);
  readonly recommendations = signal<Producto[]>([]);
  readonly isLoading = signal(true);
  readonly hasError = signal(false);
  quantity = 1;
  readonly selectedImageIndex = signal(0);

  private readonly fallbackImages = [
    '/assets/images/Imagen1.png',
    '/assets/images/ColeccionDeAutor.png',
    '/assets/images/AmaderadoOlfativas.png',
  ];

  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      this.isLoading.set(false);
      return;
    }

    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.hasError.set(true);
      this.isLoading.set(false);
      return;
    }

    this.catalog.getProductById(id).subscribe({
      next: (product) => {
        this.product.set(product);
        this.selectedImageIndex.set(0);
        this.isLoading.set(false);
      },
      error: () => {
        this.hasError.set(true);
        this.isLoading.set(false);
      },
    });

    this.catalog.getFeaturedProducts().subscribe({
      next: (products) => this.recommendations.set((products ?? []).filter((item) => item.idProducto !== id).slice(0, 3)),
      error: () => undefined,
    });
  }

  imageFor(product: Producto, index = 0): string {
    return product.imagenes?.[index] || (index === 0 ? product.imagenUrl : null) || this.fallbackImages[index % this.fallbackImages.length];
  }

  get productImages(): string[] {
    const current = this.product();
    return current ? [0, 1, 2, 3].map((index) => this.imageFor(current, index)) : this.fallbackImages;
  }

  selectImage(index: number): void {
    this.selectedImageIndex.set(index);
  }

  get selectedImage(): string {
    return this.productImages[this.selectedImageIndex()] || this.fallbackImages[0];
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 2 }).format(price || 0);
  }

  increment(): void {
    const stock = this.product()?.stock ?? 1;
    if (this.quantity < stock) this.quantity++;
  }

  decrement(): void {
    if (this.quantity > 1) this.quantity--;
  }

  addToCart(): void {
    const current = this.product();
    if (current) this.cart.add(current, this.quantity);
  }
}
