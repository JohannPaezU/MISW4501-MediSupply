import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { CardComponent } from '../../components/card/card.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule,RouterOutlet, SidebarComponent,CardComponent],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MainLayoutComponent {
  userName = 'Juan Sebastian';
}
