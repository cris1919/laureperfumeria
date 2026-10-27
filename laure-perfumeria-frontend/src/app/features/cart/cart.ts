import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { CartItem, CartService } from '../../core/services/cart.service';
import { Footer } from '../../shared/components/footer/footer';
import { Header } from '../../shared/components/header/header';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [Header, Footer, RouterLink],
  templateUrl: './cart.html',
  styleUrl: './cart.css',
})
export class Cart {
  readonly cart = inject(CartService);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);
  readonly isCheckingOut = signal(false);
  readonly checkoutError = signal('');

  image(item: CartItem): string {
    return item.product.imagenes?.[0] || item.product.imagenUrl || '/assets/images/Imagen1.png';
  }

  price(value: number): string {
    return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 2 }).format(value || 0);
  }

  increase(item: CartItem): void {
    this.cart.updateQuantity(item.product.idProducto, item.quantity + 1);
  }

  decrease(item: CartItem): void {
    this.cart.updateQuantity(item.product.idProducto, item.quantity - 1);
  }

  realizePurchase(): void {
    const user = this.auth.currentUser();
    if (!user || user.rol?.toUpperCase() !== 'CLIENTE') {
      void this.router.navigateByUrl('/login');
      return;
    }
    if (this.cart.items().length === 0) return;
    this.isCheckingOut.set(true);
    this.checkoutError.set('');
    this.cart.checkout(user.idUsuario).subscribe({
      next: (response) => {
        localStorage.setItem('laure_last_purchase_items', JSON.stringify(this.cart.items()));
        localStorage.setItem('laure_id_pedido', String(response.idPedido));
        localStorage.setItem('laure_id_venta', String(response.idVenta));
        this.cart.clear();
        this.isCheckingOut.set(false);
        void this.router.navigateByUrl('/confirmacion');
      },
      error: (error) => {
        this.isCheckingOut.set(false);
        this.checkoutError.set(error?.error?.message || error?.error?.error || (typeof error?.error === 'string' ? error.error : '') || 'No se pudo registrar la compra.');
      },
    });
  }
}
