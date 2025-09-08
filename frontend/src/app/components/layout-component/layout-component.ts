import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { Navbar } from '../navbar/navbar';
import { AuthService } from '../../features/auth/Services/auth.service';

@Component({
  selector: 'app-layout-component',
  standalone: true,
  imports: [
    RouterOutlet,
    Navbar
  ],
  templateUrl: './layout-component.html',
  styleUrl: './layout-component.css'
})
export class LayoutComponent implements OnInit {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // The AuthService automatically checks authentication state on initialization
    // No need to call checkSession here
  }
}
