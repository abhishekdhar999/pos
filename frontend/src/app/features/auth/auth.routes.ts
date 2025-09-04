import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from './signup/signup';
import { LoginComponent } from './login/login';
import { LayoutComponent } from '../../components/layout-component/layout-component';
import { ReverseAuthGuard } from './guards/reverse-auth.guard';

export const routes: Routes = [
  { 
    path: 'login', 
    component: LoginComponent,
    canActivate: [ReverseAuthGuard]
  },
  { 
    path: 'signup', 
    component: SignupComponent,
    canActivate: [ReverseAuthGuard]
  },
  { 
    path: '', 
    redirectTo: 'login', 
    pathMatch: 'full' 
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutes {

}
