import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {OrderList} from './pages/order-list/order-list';

export const routes: Routes = [
  // { path: '', component: ClientsDashboard },      // /clients (landing page)
  { path: '', component: OrderList },   // /clients/list
  // { path: 'new', component: ClientForm },    // /clients/new
  // { path: ':id/edit', component: ClientForm }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrderRoute {

}
