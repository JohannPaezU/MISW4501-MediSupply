import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReporteResultado } from '../../../interfaces/reporteResultado.interface';

@Component({
  selector: 'app-crear-productos-masivo',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './crear-producto-masivo.component.html',
  styleUrls: ['./crear-producto-masivo.component.css']
})
export class CrearProductoMasivoComponent {
  archivoSeleccionado: File | null = null;
  nombreArchivo: string = '';
  mostrarReporte: boolean = false;

  reporte: ReporteResultado = {
    totalRegistros: 10,
    registrosExitosos: 8,
    cantidadErrores: 2,
    errores: [
      'Lineal 3: Falta fecha de vencimiento',
      'Línea 7: País no reconocido'
    ]
  };

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.archivoSeleccionado = input.files[0];
      this.nombreArchivo = this.archivoSeleccionado.name;
      console.log('Archivo seleccionado:', this.nombreArchivo);
    }
  }

  triggerFileInput(): void {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput?.click();
  }

  crearProducto(): void {
    if (this.archivoSeleccionado) {
      console.log('Creando productos desde archivo:', this.nombreArchivo);
      // Aquí iría la lógica para procesar el archivo
      this.mostrarReporte = true;
    } else {
      console.log('No hay archivo seleccionado');
    }
  }
}
