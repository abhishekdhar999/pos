import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClientRoutes } from './client.routes';
import {ClientForm} from './pages/client-form/client-form';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ClientRoutes,
    ClientForm,
  ]
})
export class ClientsModule { }
