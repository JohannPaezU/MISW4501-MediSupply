import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login/login.component';
import { RecuperarContrasenaComponent } from './pages/login/recuperar-contrasena/recuperar-contrasena.component';
import { HomeComponent } from './pages/home/home/home.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'recuperar-contrasena', component: RecuperarContrasenaComponent },
      { path: '', redirectTo: '/login', pathMatch: 'full' },
    ],
  },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: 'home', component: HomeComponent },

    ],
  },
  { path: '**', redirectTo: '/home' },
];
