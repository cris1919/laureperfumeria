import { Component, OnInit, inject, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CatalogService } from '../../core/services/catalog.service';
import { Producto } from '../../shared/models/producto.model';
import { Header } from '../../shared/components/header/header';
import { Footer } from '../../shared/components/footer/footer';
import { ProductCard } from '../../shared/components/product-card/product-card';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [Header, Footer, ProductCard, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly catalog = inject(CatalogService);
  private readonly platformId = inject(PLATFORM_ID);
  readonly products = signal<Producto[]>([]);
  readonly isLoading = signal(true);
  readonly hasError = signal(false);
  readonly familyCards = [
    { name: 'Floral & Rosa', description: 'Elegante y romántica' },
    { name: 'Amaderada & Oud', description: 'Profunda y sofisticada' },
    { name: 'Cítrica', description: 'Fresca y luminosa' },
    { name: 'Oriental & Ámbar', description: 'Cálida y envolvente' },
    { name: 'Gourmand Vainilla', description: 'Dulce y memorable' },
  ];

  private readonly familyImages = [
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

    this.loadFeaturedProducts();
  }

  loadFeaturedProducts(): void {
    this.isLoading.set(true);
    this.hasError.set(false);
    this.catalog.getFeaturedProducts().subscribe({
      next: (products) => {
        this.products.set(products ?? []);
        this.isLoading.set(false);
      },
      error: () => {
        this.products.set([]);
        this.hasError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  get featuredProducts(): Producto[] {
    return this.products().slice(0, 4);
  }

  familyImage(index: number): string {
    return this.familyImages[index % this.familyImages.length];
  }

}
