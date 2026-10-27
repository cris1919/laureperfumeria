import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AuthService, LoginResponse } from '../../core/services/auth.service';
import { AdminSidebar } from '../../shared/components/admin-sidebar/admin-sidebar';

interface AdminOrder {
  idPedido: number;
  idCliente: number;
  fechaPedido: string;
  estadoPedido: string;
  total: number;
  detalles?: Array<{ idProducto: number; cantidad: number; precioUnitario?: number }>;
}

interface AdminProduct {
  idProducto: number;
  nombre: string;
  marca?: string;
  stock: number;
  precioVenta: number;
  estado?: string;
}

interface AdminClient { idCliente: number; nombres: string; apellidos: string; }
interface StockReport { idProducto: number; nombreProducto: string; marca?: string; stock: number; estado: string; }
interface OrderStatusReport { estado: string; cantidad: number; }
interface FinancialReport { ingresos: number; costos: number; margenBruto: number; }
interface TopProduct { idProducto: number; nombreProducto: string; cantidadVendida: number; ingresos: number; }

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, AdminSidebar],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin implements OnInit {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly user = signal<LoginResponse | null>(null);
  readonly orders = signal<AdminOrder[]>([]);
  readonly products = signal<AdminProduct[]>([]);
  readonly lowStockProducts = signal<StockReport[]>([]);
  readonly clients = signal<AdminClient[]>([]);
  readonly orderStatuses = signal<OrderStatusReport[]>([]);
  readonly topProducts = signal<TopProduct[]>([]);
  readonly financial = signal<FinancialReport>({ ingresos: 0, costos: 0, margenBruto: 0 });
  readonly revenue = signal(0);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly activeSection = signal('Dashboard');
  readonly Math = Math;
  readonly lowStock = computed(() => this.lowStockProducts());
  readonly recentOrders = computed(() => this.orders().slice(0, 5));
  readonly averageOrder = computed(() => {
    const orders = this.orders();
    return orders.length ? this.revenue() / orders.length : 0;
  });

  ngOnInit(): void {
    this.auth.loadSession();
    const current = this.auth.currentUser();
    if (!current || current.rol?.toUpperCase() !== 'ADMIN') {
      void this.router.navigateByUrl('/login');
      return;
    }
    this.user.set(current);
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.error.set('');
    forkJoin({
      orders: this.auth.getAuthenticated<AdminOrder[]>('/api/pedidos/admin'),
      products: this.auth.getAuthenticated<AdminProduct[]>('/api/productos'),
      clients: this.auth.getAuthenticated<AdminClient[]>('/api/clientes'),
      revenue: this.auth.getAuthenticated<number>('/api/ventas/total'),
      lowStock: this.auth.getAuthenticated<StockReport[]>('/api/reportes/stock-bajo?limite=5'),
      statuses: this.auth.getAuthenticated<OrderStatusReport[]>('/api/reportes/pedidos-por-estado'),
      topProducts: this.auth.getAuthenticated<TopProduct[]>('/api/reportes/productos-mas-vendidos?limite=5'),
      financial: this.auth.getAuthenticated<FinancialReport>('/api/reportes/margen-bruto'),
    }).subscribe({
      next: (data) => {
        this.orders.set([...data.orders].sort((a, b) => new Date(b.fechaPedido).getTime() - new Date(a.fechaPedido).getTime()));
        this.products.set(data.products);
        this.lowStockProducts.set(data.lowStock);
        this.clients.set(data.clients);
        this.revenue.set(Number(data.revenue) || 0);
        this.orderStatuses.set(data.statuses);
        this.topProducts.set(data.topProducts);
        this.financial.set(data.financial);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.error.set('No se pudo cargar la información administrativa. Verifica tu sesión y el backend.');
      },
    });
  }

  downloadReport(): void {
    this.auth.downloadAdminExcel().subscribe({
      next: (file) => {
        const url = URL.createObjectURL(file);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'reporte-laure.xlsx';
        link.click();
        URL.revokeObjectURL(url);
      },
      error: () => this.error.set('No se pudo descargar el reporte. Verifica tu sesión y el backend.'),
    });
  }

  selectSection(section: string): void {
    this.activeSection.set(section);
    if (section === 'Dashboard') {
      void this.router.navigateByUrl('/admin');
      return;
    }
    if (section === 'Catálogo') {
      void this.router.navigateByUrl('/admin/catalogo');
      return;
    }
    if (section === 'Pedidos') {
      void this.router.navigateByUrl('/admin/pedidos');
      return;
    }
    if (section === 'Usuarios') {
      void this.router.navigateByUrl('/admin/usuarios');
      return;
    }
    if (section === 'Login') return;
    if (section !== 'Dashboard') this.error.set('Esta sección está lista para conectarse a sus operaciones administrativas.');
  }

  logout(): void {
    this.auth.clearSession();
    void this.router.navigateByUrl('/login');
  }

  orderLabel(order: AdminOrder): string { return `#LP-${new Date(order.fechaPedido).getFullYear()}-${String(order.idPedido).padStart(6, '0')}`; }

  statusLabel(status: string): string {
    return status.replaceAll('_', ' ').toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase());
  }
}
