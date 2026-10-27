import { Component, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { signal } from '@angular/core';
import { filter } from 'rxjs';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-cart-floating',
  standalone: true,
  imports: [RouterLink],
  template: `@if (!isAdmin() && cart.totalCount() > 0) {<a class="cart-floating" routerLink="/carrito" aria-label="Abrir carrito"><img src="/assets/icons/Bolsa.png" alt="" /><span class="cart-badge">{{ cart.totalCount() }}</span></a>}`,
  styles: [`.cart-floating{position:fixed;z-index:30;right:26px;bottom:26px;display:grid;place-items:center;width:52px;height:52px;border-radius:50%;background:#292b27;box-shadow:0 8px 20px #25251f3d}.cart-floating img{width:24px;height:24px;object-fit:contain}.cart-badge{position:absolute;top:-5px;right:-4px;display:grid;place-items:center;width:21px;height:21px;border-radius:50%;background:#b1842c;color:#fff;font:600 11px Arial,sans-serif}@media(max-width:520px){.cart-floating{right:16px;bottom:16px}}`],
})
export class CartFloating {
  readonly cart = inject(CartService);
  private readonly router = inject(Router);
  readonly isAdmin = signal(this.router.url.startsWith('/admin'));

  constructor() {
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe((event) => {
      this.isAdmin.set((event as NavigationEnd).urlAfterRedirects.startsWith('/admin'));
    });
  }
}
