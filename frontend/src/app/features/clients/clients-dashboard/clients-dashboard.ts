import {Component, OnInit} from '@angular/core';
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-clients-dashboard',
    imports: [
        RouterLink
    ],
  templateUrl: './clients-dashboard.html',
  styleUrl: './clients-dashboard.css'
})
export class ClientsDashboard implements OnInit{
  isSupervisor = false;

  ngOnInit(): void {
    const role = sessionStorage.getItem('role'); // 👈 get role from sessionStorage
    this.isSupervisor = role === 'supervisor';   // 👈 check if supervisor
  }
}
