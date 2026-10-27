import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/home').then((module) => module.Home),
  },
  {
    path: 'catalogo',
    loadComponent: () =>
      import('./features/products/products').then((module) => module.Products),
  },
  {
    path: 'producto/:id',
    loadComponent: () =>
      import('./features/product-detail/product-detail').then((module) => module.ProductDetail),
  },
  {
    path: 'nosotros',
    loadComponent: () =>
      import('./features/about/about').then((module) => module.About),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login').then((module) => module.Login),
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./features/register/register').then((module) => module.Register),
  },
  {
    path: 'carrito',
    loadComponent: () =>
      import('./features/cart/cart').then((module) => module.Cart),
  },
  {
    path: 'confirmacion',
    loadComponent: () =>
      import('./features/confirmation/confirmation').then((module) => module.Confirmation),
  },
  {
    path: 'perfil',
    loadComponent: () =>
      import('./features/profile/profile').then((module) => module.Profile),
  },
  {
    path: 'historial',
    loadComponent: () =>
      import('./features/purchase-history/purchase-history').then((module) => module.PurchaseHistory),
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./features/admin/admin').then((module) => module.Admin),
  },
  {
    path: 'admin/catalogo',
    loadComponent: () =>
      import('./features/admin-catalog/admin-catalog').then((module) => module.AdminCatalog),
  },
  {
    path: 'admin/usuarios',
    loadComponent: () =>
      import('./features/admin-users/admin-users').then((module) => module.AdminUsers),
  },
  {
    path: 'admin/pedidos',
    loadComponent: () =>
      import('./features/admin-orders/admin-orders').then((module) => module.AdminOrders),
  },
  { path: '**', redirectTo: '' },
];
