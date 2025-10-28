import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [MatIcon],
  templateUrl: './reportes.component.html',
  styleUrl: './reportes.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportesComponent { }
