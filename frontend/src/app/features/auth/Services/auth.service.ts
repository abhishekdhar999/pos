import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthModel } from '../models/auth.model';
import { SafeStorageService } from './SafeStorageService';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/session/login';
  private loggedIn = new BehaviorSubject<boolean>(false);
  public loggedIn$ = this.loggedIn.asObservable();

  constructor(private http: HttpClient, private router: Router, private safeStorage: SafeStorageService) {
    console.log('AuthService initialized');
    // Check if user is already logged in on service initialization
    this.checkInitialAuthState();
  }

  private checkInitialAuthState() {
    const email = this.safeStorage.getItem('email');
    const role = this.safeStorage.getItem('role');
    const lastCheckTime = this.safeStorage.getItem('lastCheckTime');

    console.log('Checking initial auth state:', { email, role, lastCheckTime });

    if (email && role && lastCheckTime && this.isSessionValid()) {
      console.log('Local session is valid, setting loggedIn to true');
      this.loggedIn.next(true);
    } else {
      console.log('Local session is invalid, setting loggedIn to false');
      this.loggedIn.next(false);
      // Clear any invalid data
      this.clearAuthData();
    }
  }

  signup(authModel: AuthModel): Observable<AuthModel> {
    return this.http.post<AuthModel>(this.apiUrl, authModel, {
      withCredentials: true
    });
  }

  saveAuthData(email: string, role: string) {
    console.log('Saving auth data:', { email, role });
    const now = new Date().getTime();
    this.safeStorage.setItem('email', email);
    this.safeStorage.setItem('role', role);
    this.safeStorage.setItem('lastCheckTime', now.toString());

    // Verify data was saved
    console.log('Verifying saved data:', {
      email: this.safeStorage.getItem('email'),
      role: this.safeStorage.getItem('role'),
      lastCheckTime: this.safeStorage.getItem('lastCheckTime')
    });

    this.loggedIn.next(true);
  }

  logout() {
    console.log('Logging out user');
    this.http.get('http://localhost:8080/pos/session/logout', { withCredentials: true })
      .subscribe({
        next: () => {
          this.clearAuthData();
          this.router.navigate(['/login']);
        },
        error: () => {
          this.clearAuthData();
          this.router.navigate(['/login']);
        }
      });
  }

  private clearAuthData() {
    console.log('Clearing auth data');
    this.safeStorage.clear();
    this.loggedIn.next(false);
  }

  isSessionValid(): boolean {
    const lastCheck = this.safeStorage.getItem('lastCheckTime');
    if (!lastCheck) return false;

    const lastCheckTime = Number(lastCheck);
    const now = new Date().getTime();
    const isValid = now - lastCheckTime < 24 * 60 * 60 * 1000;

    console.log('Session validation:', { lastCheckTime, now, isValid, timeDiff: now - lastCheckTime });

    // Session valid for 24 hours (86400000 ms)
    return isValid;
  }

  checkSession() {
    console.log('checkSession called');
    // Only check backend if we have local data
    const email = this.safeStorage.getItem('email');
    const role = this.safeStorage.getItem('role');

    if (!email || !role) {
      console.log('No local auth data, setting loggedIn to false');
      this.loggedIn.next(false);
      return;
    }

    // If local data is valid, don't override it with backend check
    if (this.isSessionValid()) {
      console.log('Local session is valid, keeping loggedIn as true');
      this.loggedIn.next(true);
      return;
    }

    console.log('Local session expired, checking with backend');
    // Only call backend if local session is expired
    return this.http
      .get('http://localhost:8080/pos/api/auth/me', { withCredentials: true })
      .subscribe({
        next: () => {
          console.log('Backend session check successful, extending session');
          // Update the timestamp to extend the session
          const now = new Date().getTime();
          this.safeStorage.setItem('lastCheckTime', now.toString());
          this.loggedIn.next(true);
        },
        error: (error) => {
          console.warn('Backend session check failed:', error);
          // Only clear data if backend explicitly says session is invalid
          // Don't clear data on network errors or temporary failures
          if (!this.isSessionValid()) {
            console.log('Clearing auth data due to expired session');
            this.clearAuthData();
          } else {
            console.log('Keeping local session despite backend failure');
          }
        }
      });
  }

  getCurrentUser() {
    return {
      email: this.safeStorage.getItem('email'),
      role: this.safeStorage.getItem('role')
    };
  }

  // Synchronous method to check if user is authenticated
  isAuthenticated(): boolean {
    const email = this.safeStorage.getItem('email');
    const role = this.safeStorage.getItem('role');
    const lastCheckTime = this.safeStorage.getItem('lastCheckTime');

    return !!(email && role && lastCheckTime && this.isSessionValid());
  }
}
