import { Routes } from '@angular/router';
import { Home } from './features/dashboard/home/home';
import { SignupComponent } from './features/auth/signup/signup';
import { LayoutComponent } from './components/layout-component/layout-component';
import { AuthGuard } from './features/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home'
      },
      {
        path: 'home',
        component: Home,
        canActivate: [AuthGuard]
      },
      {
        path: 'clients',
        loadChildren: () =>
          import('./features/clients/client.routes').then(m => m.routes),
        canActivate: [AuthGuard]
      },
      {
        path: 'products',
        loadChildren: () =>
          import('./features/products/product.route').then(m => m.routes),
        canActivate: [AuthGuard]
      },
      {
        path: 'login',
        loadChildren: () =>
          import('./features/auth/auth.routes').then(m => m.routes)
      },
      {
        path: 'signup',
        loadChildren: () =>
          import('./features/auth/auth.routes').then(m => m.routes)
      },
      {
        path:'orders',
        loadChildren:()=>
          import('./features/orders/order.route').then(m=>m.routes)
      }
    ]
  }
];
