import { DatePipe } from '@angular/common';
import { isPlatformBrowser } from '@angular/common';
import { Component, OnInit, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartItem, CartService } from '../../core/services/cart.service';
import { AuthService, ClientProfile } from '../../core/services/auth.service';
import { Footer } from '../../shared/components/footer/footer';
import { Header } from '../../shared/components/header/header';

@Component({
  selector: 'app-confirmation',
  standalone: true,
  imports: [Header, Footer, RouterLink, DatePipe],
  templateUrl: './confirmation.html',
  styleUrl: './confirmation.css',
})
export class Confirmation implements OnInit {
  readonly cart = inject(CartService);
  private readonly auth = inject(AuthService);
  private readonly platformId = inject(PLATFORM_ID);
  readonly profile = signal<ClientProfile | null>(null);
  readonly purchasedItems = signal<CartItem[]>([]);
  readonly displayCount = computed(() => this.purchasedItems().reduce((total, item) => total + item.quantity, 0));
  readonly displayTotal = computed(() => this.purchasedItems().reduce((total, item) => total + (item.product.precioVenta || 0) * item.quantity, 0));
  readonly orderNumber = signal('LP-2026-000000');
  readonly today = new Date();
  readonly invoiceError = signal('');

  ngOnInit(): void {
    this.auth.loadSession();
    if (isPlatformBrowser(this.platformId)) {
      const rawItems = localStorage.getItem('laure_last_purchase_items');
      if (rawItems) {
        try { this.purchasedItems.set(JSON.parse(rawItems) as CartItem[]); } catch { this.purchasedItems.set([]); }
      }
      const idPedido = Number(localStorage.getItem('laure_id_pedido'));
      if (idPedido) {
        this.orderNumber.set(`LP-${new Date().getFullYear()}-${String(idPedido).padStart(6, '0')}`);
        // La confirmación es el último respaldo: la compra ya registrada no debe conservar productos en la bolsa.
        this.cart.clear();
      }
    }
    const user = this.auth.currentUser();
    if (isPlatformBrowser(this.platformId) && user?.rol?.toUpperCase() === 'CLIENTE') {
      this.auth.getClientProfile(user.idUsuario).subscribe({ next: (profile) => this.profile.set(profile), error: () => undefined });
    }
  }

  image(imageUrl: string | null, images?: string[]): string {
    return images?.[0] || imageUrl || '/assets/images/Imagen1.png';
  }

  price(value: number): string {
    return new Intl.NumberFormat('es-PE', { style: 'currency', currency: 'PEN', maximumFractionDigits: 2 }).format(value || 0);
  }

  downloadInvoice(): void {
    if (typeof window === 'undefined') return;
    const idVenta = Number(localStorage.getItem('laure_id_venta'));
    if (!idVenta) {
      this.invoiceError.set('No se encontró el identificador de la venta.');
      return;
    }
    this.auth.downloadInvoice(idVenta).subscribe({
      next: (pdf) => {
        const url = URL.createObjectURL(pdf);
        const link = document.createElement('a');
        link.href = url;
        link.download = `factura-venta-${idVenta}.pdf`;
        link.click();
        URL.revokeObjectURL(url);
      },
      error: () => this.invoiceError.set('No se pudo descargar la factura electrónica.'),
    });
  }
}
