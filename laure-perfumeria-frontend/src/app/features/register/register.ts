import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Footer } from '../../shared/components/footer/footer';
import { Header } from '../../shared/components/header/header';

@Component({ selector: 'app-register', standalone: true, imports: [FormsModule, RouterLink, Header, Footer], templateUrl: './register.html', styleUrl: './register.css' })
export class Register {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  form = { nombres: '', apellidos: '', correo: '', password: '', confirmPassword: '', telefono: '', direccion: '' };
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal('');

  submit(): void {
    this.errorMessage.set('');
    if (this.form.password !== this.form.confirmPassword) { this.errorMessage.set('Las contraseñas no coinciden.'); return; }
    if (this.form.password.length < 6) { this.errorMessage.set('La contraseña debe tener al menos 6 caracteres.'); return; }
    this.isSubmitting.set(true);
    this.auth.registerClient({ nombres: this.form.nombres.trim(), apellidos: this.form.apellidos.trim(), correo: this.form.correo.trim(), password: this.form.password, telefono: this.form.telefono.trim(), direccion: this.form.direccion.trim() }).subscribe({
      next: () => { this.isSubmitting.set(false); void this.router.navigateByUrl('/login'); },
      error: (error) => { this.isSubmitting.set(false); this.errorMessage.set(error?.error?.message || error?.error?.error || 'No se pudo crear la cuenta.'); },
    });
  }
}
