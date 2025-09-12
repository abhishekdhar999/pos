import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {OrderList} from './pages/order-list/order-list';
import {SalesReportComponent} from './pages/sales-report/sales-report';
import {ReportsComponent} from './pages/reports/reports';

export const routes: Routes = [
  { path: '', component: OrderList },   // /orders
  { path: 'sales-report', component: SalesReportComponent },   // /orders/sales-report
  { path: 'reports', component: ReportsComponent },   // /orders/reports
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrderRoute {

}
