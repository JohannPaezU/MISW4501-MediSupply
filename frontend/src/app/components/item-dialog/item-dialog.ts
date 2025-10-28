import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-item-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './item-dialog.html',
  styleUrl: './item-dialog.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ItemDialog { }
