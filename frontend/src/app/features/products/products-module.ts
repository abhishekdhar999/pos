import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {ProductRoutes} from './product.route';
import {ProductForm} from './pages/product-form/product-form';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
   ProductRoutes,
    ProductForm
  ]
})
export class ProductsModule {
  
}
