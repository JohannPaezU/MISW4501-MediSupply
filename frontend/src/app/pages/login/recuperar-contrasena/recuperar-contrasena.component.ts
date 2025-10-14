import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-recuperar-contrasena',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './recuperar-contrasena.component.html',
  styleUrls: ['./recuperar-contrasena.component.css']
})
export class RecuperarContrasenaComponent {
  recoverForm: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {
    this.recoverForm = this.fb.group({
      email: [''],
    });
  }

  onSubmit() {
    console.log('Correo enviado para recuperar acceso:', this.recoverForm.value);
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
