import { isPlatformBrowser } from '@angular/common';
import { Component, PLATFORM_ID, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Footer } from '../../shared/components/footer/footer';
import { Header } from '../../shared/components/header/header';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink, Header, Footer],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly platformId = inject(PLATFORM_ID);

  correo = '';
  password = '';
  remember = false;
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  submit(): void {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.isSubmitting.set(true);

    this.auth.login({ correo: this.correo.trim(), password: this.password }).subscribe({
      next: (response) => {
        if (isPlatformBrowser(this.platformId)) this.auth.saveSession(response, this.remember);
        this.isSubmitting.set(false);
        this.successMessage.set('Sesión iniciada correctamente.');
        void this.router.navigateByUrl(response.rol?.toUpperCase() === 'ADMIN' ? '/admin' : '/catalogo');
      },
      error: (error) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(error?.error?.message || error?.error?.error || 'Correo o contraseña incorrectos.');
      },
    });
  }
}
