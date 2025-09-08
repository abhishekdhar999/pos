import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../features/auth/Services/auth.service';
import { AsyncPipe } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    AsyncPipe
  ],
  templateUrl: '/navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar implements OnInit {
  isLoggedIn = false;
  private authSubscription: Subscription;

  constructor(protected authService: AuthService) {
    this.authSubscription = new Subscription();
  }

  ngOnInit() {
    console.log('Navbar initialized');
    // Subscribe to authentication state changes
    this.authSubscription = this.authService.loggedIn$.subscribe(status => {
      console.log('Navbar received auth state change:', status);
      this.isLoggedIn = status;
    });


  }

  logout() {
    this.authService.logout();
  }


}
