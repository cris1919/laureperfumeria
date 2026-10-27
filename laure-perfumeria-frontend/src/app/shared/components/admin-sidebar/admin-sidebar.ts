import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-admin-sidebar',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './admin-sidebar.html',
  styleUrl: './admin-sidebar.css',
})
export class AdminSidebar {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  @Input() activeSection = 'Dashboard';
  @Output() sectionSelected = new EventEmitter<string>();
  readonly menuItems = ['Dashboard', 'Pedidos', 'Catálogo', 'Usuarios'];

  selectSection(section: string): void {
    const paths: Record<string, string> = {
      Dashboard: '/admin',
      Catálogo: '/admin/catalogo',
      Pedidos: '/admin/pedidos',
      Usuarios: '/admin/usuarios',
    };
    if (paths[section]) {
      void this.router.navigateByUrl(paths[section]);
      return;
    }
    this.sectionSelected.emit(section);
  }
  logout(): void { this.auth.clearSession(); void this.router.navigateByUrl('/login'); }
}
