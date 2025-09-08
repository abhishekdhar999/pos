import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../Services/auth.service';
import { map, take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ReverseAuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate() {
    return this.authService.loggedIn$.pipe(
      take(1),
      map(isLoggedIn => {
        if (isLoggedIn) {
          this.router.navigate(['/home']);
          return false;
        } else {
          return true;
        }
      })
    );
  }
}
