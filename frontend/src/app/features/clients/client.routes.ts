import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ClientForm} from './pages/client-form/client-form';
import {ClientList} from './pages/client-list/client-list';
import {ClientsDashboard} from './clients-dashboard/clients-dashboard';

export const routes: Routes = [
  { path: '', component: ClientsDashboard },      // /clients (landing page)
  { path: 'list', component: ClientList },   // /clients/list
  { path: 'new', component: ClientForm },    // /clients/new
  { path: ':id/edit', component: ClientForm }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientRoutes {

}
