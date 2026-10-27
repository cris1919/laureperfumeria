import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CartFloating } from './shared/components/cart-floating/cart-floating';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CartFloating],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
