import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {Home} from './home/home';

const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },   // default → home
  { path: 'home', component: Home },

  {
    path: 'clients',
    loadChildren: () =>
      import('../clients/client.routes').then(m => m.ClientRoutes)
  },
  {
    path: 'products',
    loadChildren: () =>
      import('../products/product.route').then(m => m.ProductRoutes)
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {

}
