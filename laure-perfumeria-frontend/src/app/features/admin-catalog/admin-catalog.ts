import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { AuthService, LoginResponse } from '../../core/services/auth.service';
import { Categoria } from '../../shared/models/categoria.model';
import { Producto } from '../../shared/models/producto.model';
import { AdminSidebar } from '../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-catalog',
  standalone: true,
  imports: [CurrencyPipe, DecimalPipe, FormsModule, AdminSidebar],
  templateUrl: './admin-catalog.html',
  styleUrl: './admin-catalog.css',
  styles: ['.image-preview,.image-placeholder{display:grid;place-items:center;width:100%;height:120px;border:1px solid #ded7cb;background:#f2efe9}.image-preview{object-fit:cover}.image-placeholder{color:#aaa195;font:10px Arial,sans-serif}.status-pill.inactive{color:#78736b;background:#eceae5}.deactivate-action{color:#a24f45!important;font-size:16px!important}'],
})
export class AdminCatalog implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly user = signal<LoginResponse | null>(null);
  readonly products = signal<Producto[]>([]);
  readonly categories = signal<Categoria[]>([]);
  readonly search = signal('');
  readonly category = signal(0);
  readonly status = signal('TODOS');
  readonly loading = signal(true);
  readonly error = signal('');
  readonly formOpen = signal(false);
  readonly saving = signal(false);
  readonly formError = signal('');
  readonly formSuccess = signal('');
  readonly editingId = signal<number | null>(null);
  productForm = { idCategoria: 0, nombre: '', descripcion: '', marca: '', precioVenta: null as number | null, costoUnitario: null as number | null, stock: null as number | null, imagenes: ['', '', '', ''] };
  imageFiles: Array<File | null> = [null, null, null, null];
  imagePreviews: Array<string | null> = [null, null, null, null];
  readonly Math = Math;
  readonly filteredProducts = computed(() => {
    const term = this.search().trim().toLowerCase();
    return this.products().filter((product) => {
      const matchesTerm = !term || `${product.nombre} ${product.marca || ''} ${product.descripcion || ''}`.toLowerCase().includes(term);
      const matchesCategory = !this.category() || product.idCategoria === this.category();
      const matchesStatus = this.status() === 'TODOS' || (product.estado || '').toUpperCase() === this.status();
      return matchesTerm && matchesCategory && matchesStatus;
    });
  });
  readonly macerationCount = computed(() => this.products().filter((product) => product.estado.toUpperCase().includes('MACER')).length);
  readonly criticalCount = computed(() => this.products().filter((product) => product.stock <= 5).length);
  readonly catalogValue = computed(() => this.products().reduce((sum, product) => sum + (product.precioVenta || 0), 0));

  ngOnInit(): void {
    this.auth.loadSession();
    const current = this.auth.currentUser();
    if (!current || current.rol?.toUpperCase() !== 'ADMIN') {
      void this.router.navigateByUrl('/login');
      return;
    }
    this.user.set(current);
    this.loadCatalog();
  }

  loadCatalog(): void {
    this.loading.set(true);
    this.auth.getAuthenticated<Producto[]>('/api/productos').subscribe({
      next: (products) => { this.products.set(products); this.loading.set(false); },
      error: () => { this.error.set('No se pudo cargar el catálogo.'); this.loading.set(false); },
    });
    this.auth.getAuthenticated<Categoria[]>('/api/categorias').subscribe({ next: (categories) => this.categories.set(categories) });
  }

  openProductForm(): void {
    this.editingId.set(null);
    this.productForm = { idCategoria: 0, nombre: '', descripcion: '', marca: '', precioVenta: null, costoUnitario: null, stock: null, imagenes: ['', '', '', ''] };
    this.imageFiles = [null, null, null, null];
    this.imagePreviews = [null, null, null, null];
    this.formError.set('');
    this.formSuccess.set('');
    this.formOpen.set(true);
  }

  editProduct(product: Producto): void {
    this.editingId.set(product.idProducto);
    this.productForm = {
      idCategoria: product.idCategoria,
      nombre: product.nombre,
      descripcion: product.descripcion || '',
      marca: product.marca || '',
      precioVenta: product.precioVenta,
      costoUnitario: product.costoUnitario || 0,
      stock: product.stock,
      imagenes: [...(product.imagenes || [product.imagenUrl || '', '', '', ''])].slice(0, 4),
    };
    while (this.productForm.imagenes.length < 4) this.productForm.imagenes.push('');
    this.imageFiles = [null, null, null, null];
    this.imagePreviews = [...this.productForm.imagenes];
    this.formError.set('');
    this.formSuccess.set('');
    this.formOpen.set(true);
  }

  toggleProductStatus(product: Producto): void {
    const nextStatus = product.estado.toUpperCase() === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    this.auth.patchAuthenticated<Producto>('/api/productos/' + product.idProducto + '/estado', { estado: nextStatus }).subscribe({
      next: () => { this.formSuccess.set(`Producto ${nextStatus === 'ACTIVO' ? 'activado' : 'desactivado'} correctamente.`); this.loadCatalog(); },
      error: (error) => this.formError.set(error?.error?.message || error?.error?.error || 'No se pudo cambiar el estado del producto.'),
    });
  }

  closeProductForm(): void {
    if (!this.saving()) this.formOpen.set(false);
  }

  selectImage(event: Event, index: number): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] || null;
    this.imageFiles[index] = file;
    this.imagePreviews[index] = null;
    if (file) {
      const reader = new FileReader();
      reader.onload = () => { this.imagePreviews[index] = String(reader.result); };
      reader.readAsDataURL(file);
    }
    this.formError.set('');
  }

  submitProduct(): void {
    this.formError.set('');
    this.formSuccess.set('');
    if (!this.productForm.idCategoria || !this.productForm.nombre.trim() || this.productForm.precioVenta === null || this.productForm.costoUnitario === null || this.productForm.stock === null || this.productForm.imagenes.some((image, index) => !image.trim() && !this.imageFiles[index])) {
      this.formError.set('Completa todos los campos obligatorios y selecciona las 4 imágenes.');
      return;
    }
    this.saving.set(true);
    forkJoin(this.imageFiles.map((file, index) => file ? this.auth.uploadProductImage(file).pipe() : of({ url: this.productForm.imagenes[index] }))).subscribe({
      next: (uploadedImages) => {
        const payload = { ...this.productForm, nombre: this.productForm.nombre.trim(), marca: this.productForm.marca.trim(), imagenes: uploadedImages.map((image) => image.url) };
        const request = this.editingId() ? this.auth.putAuthenticated<Producto>('/api/productos/' + this.editingId(), payload) : this.auth.postAuthenticated<Producto>('/api/productos', payload);
        request.subscribe({
        next: () => {
        this.saving.set(false);
        this.formSuccess.set(this.editingId() ? 'Producto actualizado correctamente.' : 'Producto creado correctamente.');
        this.formOpen.set(false);
        this.productForm = { idCategoria: 0, nombre: '', descripcion: '', marca: '', precioVenta: null, costoUnitario: null, stock: null, imagenes: ['', '', '', ''] };
        this.imageFiles = [null, null, null, null];
        this.imagePreviews = [null, null, null, null];
        this.loadCatalog();
        },
        error: (error) => { this.saving.set(false); this.formError.set(error?.error?.message || error?.error?.error || 'No se pudo crear el producto.'); },
        });
      },
      error: (error) => { this.saving.set(false); this.formError.set(error?.error?.message || 'No se pudieron subir las imágenes.'); },
    });
  }

  selectSection(section: string): void {
    if (section === 'Dashboard') void this.router.navigateByUrl('/admin');
    else if (section === 'Catálogo') void this.router.navigateByUrl('/admin/catalogo');
    else if (section === 'Pedidos') void this.router.navigateByUrl('/admin/pedidos');
    else if (section === 'Usuarios') void this.router.navigateByUrl('/admin/usuarios');
  }

  logout(): void { this.auth.clearSession(); void this.router.navigateByUrl('/login'); }

  image(product: Producto): string { return product.imagenes?.[0] || product.imagenUrl || '/assets/images/Imagen1.png'; }
  categoryName(id: number): string { return this.categories().find((category) => category.idCategoria === id)?.nombre || 'Colección Laure'; }
  statusLabel(status: string): string { return status.replaceAll('_', ' ').toLowerCase().replace(/(^|\s)\S/g, (letter) => letter.toUpperCase()); }
}
