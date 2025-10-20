import { TestBed } from '@angular/core/testing';
import { SidebarComponent } from './sidebar.component';
import { Router } from '@angular/router';
import { AuthService } from '../../services/login/auth.service';
import { MenuItem } from '../../interfaces/menuItem.interface';
import { RouterTestingModule } from '@angular/router/testing';

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  // Variable para simular la URL actual
  let currentUrl = '/home';

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['logout']);

    TestBed.configureTestingModule({
      imports: [SidebarComponent, RouterTestingModule.withRoutes([])],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    const fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    // Definir url como getter dinámico una sola vez
    Object.defineProperty(router, 'url', {
      get: () => currentUrl,
      configurable: true  // permite modificar la variable currentUrl sin redefinir
    });
  });

  it('should return true if current route matches menu item route', () => {
    currentUrl = '/home';
    const item: MenuItem = { icon: 'home', label: 'Inicio', route: '/home' };
    expect(component.isActive(item)).toBeTrue();
  });

  it('should return false if current route does not match menu item route', () => {
    currentUrl = '/otra';
    const item: MenuItem = { icon: 'home', label: 'Inicio', route: '/home' };
    expect(component.isActive(item)).toBeFalse();
  });

  it('should return true if current route matches one of activeFor routes', () => {
    const item: MenuItem = {
      icon: 'storefront',
      label: 'Vendedores',
      route: '/vendedores',
      activeFor: ['/vendedores', '/vendedores/crear']
    };

    currentUrl = '/vendedores/crear';
    expect(component.isActive(item)).toBeTrue();

    currentUrl = '/vendedores';
    expect(component.isActive(item)).toBeTrue();
  });

  it('should call logout on AuthService', () => {
    component.logout();
    expect(authServiceSpy.logout).toHaveBeenCalled();
  });
});
