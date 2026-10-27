import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartService } from '../../../core/services/cart.service';
import { Producto } from '../../models/producto.model';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCard {
  private readonly cart = inject(CartService);
  readonly product = input.required<Producto>();
  readonly index = input(0);

  private readonly fallbackImages = [
    '/assets/images/Imagen1.png',
    '/assets/images/ColeccionDeAutor.png',
    '/assets/images/floral_olfativoa.png',
    '/assets/images/AmaderadoOlfativas.png',
    '/assets/images/CitricoOlfativas.png',
    '/assets/images/OrientalOfativas.png',
    '/assets/images/VainillaOlfativa.png',
  ];

  image(): string {
    const product = this.product();
    return product.imagenUrl || this.fallbackImages[this.index() % this.fallbackImages.length];
  }

  price(): string {
    return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 2 }).format(this.product().precioVenta || 0);
  }

  addToCart(): void {
    this.cart.add(this.product());
  }
}
