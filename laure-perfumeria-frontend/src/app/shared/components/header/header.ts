import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly isClient = computed(() => this.auth.currentUser()?.rol?.toUpperCase() === 'CLIENTE');
  readonly profileMenuOpen = signal(false);

  ngOnInit(): void {
    this.auth.loadSession();
  }

  toggleProfileMenu(): void {
    this.profileMenuOpen.update((open) => !open);
  }

  logout(): void {
    this.auth.clearSession();
    this.profileMenuOpen.set(false);
    void this.router.navigateByUrl('/login');
  }
}
