import { Component } from '@angular/core';
import { Header } from '../../shared/components/header/header';
import { Footer } from '../../shared/components/footer/footer';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [Header, Footer],
  templateUrl: './about.html',
  styleUrl: './about.css',
})
export class About {
  readonly pillars = [
    { icon: '/assets/icons/maduracion.png', number: '01', title: 'Maduración Paciente', text: 'Dejamos que nuestros extractos alcancen su punto de equilibrio y carácter.' },
    { icon: '/assets/icons/Nobleza.png', number: '02', title: 'Materias Primas Puras', text: 'Seleccionamos ingredientes nobles de origen natural, con la más alta calidad.' },
    { icon: '/assets/icons/Potencia.png', number: '03', title: 'Extractos de Parfum', text: 'Concentraciones extraordinarias que permanecen en la piel con intensidad.' },
    { icon: '/assets/icons/Singularidad.png', number: '04', title: 'Ediciones Numeradas', text: 'Cada creación es limitada y lleva una historia irrepetible.' },
  ];

  readonly masters = [
    { image: '/assets/images/sobreNosotros4.png', place: 'Grasse, Francia', name: 'Édouard Valmont', text: 'Formado en la tradición más refinada de Grasse, convierte cada materia en una firma inolvidable.' },
    { image: '/assets/images/sobreNosotros3.png', place: 'París, Francia', name: 'Valeria de la Lova', text: 'Especialista en destilación molecular y composición floral, construye armonías que cuentan historias.' },
    { image: '/assets/images/sobreNosotros6.png', place: 'Florencia, Italia', name: 'Matteo Corsini', text: 'Herencia de boticarios florentinos y sensibilidad moderna en cada acorde que formula.' },
  ];
}
