import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../Services/auth.service';
import { HttpClient } from '@angular/common/http';
import { ToastService } from '../../../components/errormodel/toast.service';
import {AuthModel} from '../models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent {

  authForm: FormGroup;

  isLoading = false;


  constructor(
    private authService: AuthService,
    private router: Router,
    private http: HttpClient,
    private toastService: ToastService,
    private fb: FormBuilder,
  ) {
    this.authForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(2)]]
    });
  }


  onSubmit() {
    console.log('Submit clicked', this.authForm.value);
    if (this.authForm.valid) {
      this.isLoading = true;

      const authData: AuthModel = this.authForm.value;
      // Call login API (using signup endpoint as per your setup)
      this.http.post('http://localhost:8080/pos/session/login', authData, {
        withCredentials: true
      }).subscribe({
        next: (response: any) => {
          console.log('Login response:', response);
          this.isLoading = false;
          if (response.email && response.role) {
            console.log('Login successful, saving auth data');
            this.authService.saveAuthData(response.email, response.role);
            this.toastService.success('Login successful!');
            console.log('Redirecting to home');
            this.router.navigate(['/home']);
          } else {
            console.error('Invalid response format:', response);
            this.toastService.error('Invalid response from server');
          }
        },
        error: (error) => {
          console.error('Login error:', error);
          this.isLoading = false;
          if (error.status === 401) {
            this.toastService.error('Invalid email or password');
          } else {
            this.toastService.error('Login failed. Please try again.');
          }
        }
      });
    }
  }
}
