import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ProductForm} from './pages/product-form/product-form';
import {ProductList} from './pages/product-list/product-list';
import {ProductsDashboard} from './products-dashboard/products-dashboard';


export const routes: Routes = [
  { path: '', component: ProductsDashboard },      // /clients (landing page)
  { path: 'list', component: ProductList },   // /clients/list
  { path: 'new', component:ProductForm },    // /clients/new
  { path: ':id/edit', component: ProductForm }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProductRoutes {

}
