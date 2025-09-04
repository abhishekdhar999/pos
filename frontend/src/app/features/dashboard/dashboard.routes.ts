import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {Home} from './home/home';
import {SignupComponent} from '../auth/signup/signup';

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
  {
    path:'orders',
    loadChildren:()=>
      import('../orders/order.route').then(m=>m.OrderRoute)
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutes {

}
