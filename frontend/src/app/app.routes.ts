import { Routes } from '@angular/router';

import {Home} from './features/dashboard/home/home';
export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }, // default route
  { path: 'dashboard', component: Home },

  {
    path: 'clients',
    loadChildren: () =>
      import('./features/clients/client.routes').then(m => m.routes)
  },


  {
    path: 'products',
    loadChildren: () =>
      import('./features/products/product.route').then(m => m.routes)
  },
  //
  // // wildcard (optional)
  // { path: '**', redirectTo: 'dashboard' }
];
