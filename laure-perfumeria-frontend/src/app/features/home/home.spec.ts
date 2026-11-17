import { CUSTOM_ELEMENTS_SCHEMA, PLATFORM_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, RouterLink } from '@angular/router';
import { Subject } from 'rxjs';
import { CatalogService } from '../../core/services/catalog.service';
import { Producto } from '../../shared/models/producto.model';
import { Home } from './home';

describe('Home', () => {
  let requests: Subject<Producto[]>[];
  let catalog: { getFeaturedProducts: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    requests = [];
    catalog = {
      getFeaturedProducts: vi.fn(() => {
        const request = new Subject<Producto[]>();
        requests.push(request);
        return request.asObservable();
      }),
    };

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        { provide: CatalogService, useValue: catalog },
        { provide: PLATFORM_ID, useValue: 'browser' },
        provideRouter([]),
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
      .overrideComponent(Home, {
        set: { imports: [RouterLink], schemas: [CUSTOM_ELEMENTS_SCHEMA] },
      })
      .compileComponents();
  });

  it('distinguishes catalog load errors and retries the request', () => {
    const fixture = TestBed.createComponent(Home);
    fixture.detectChanges();

    requests[0].error(new Error('API unavailable'));
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'No pudimos cargar los perfumes destacados',
    );

    (fixture.nativeElement as HTMLElement).querySelector('button')?.click();
    fixture.detectChanges();
    expect(catalog.getFeaturedProducts).toHaveBeenCalledTimes(2);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'Cargando la selección de Laure',
    );

    requests[1].next([]);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'Aún no hay perfumes disponibles en el catálogo.',
    );
  });
});
