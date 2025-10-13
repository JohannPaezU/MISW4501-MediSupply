import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';



export const homeRoutes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },

  { path: '**', redirectTo: 'home', pathMatch: 'full' },


];
@NgModule({
  imports: [RouterModule.forChild(homeRoutes)],
  exports: [RouterModule]
})
export class HomeR { }
