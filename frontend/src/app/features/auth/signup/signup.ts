import { Component } from '@angular/core';
import { AuthService } from '../Services/auth.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthModel } from '../models/auth.model';
import { Router } from '@angular/router';
import { ToastService } from '../../../components/errormodel/toast.service';
import {Home} from '../../dashboard/home/home';
@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './signup.html',
  styleUrls: ['./signup.css']
})
export class SignupComponent {
  authForm: FormGroup;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private toastService: ToastService
  ) {
    this.authForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSignup() {

    if (this.authForm.valid) {
      this.isLoading = true;

      const authData: AuthModel = this.authForm.value;

      this.authService.signup(authData).subscribe({
        next: (res: any) => {
          this.isLoading = false;
          if (res.email && res.role) {
            this.toastService.success('Signup successful!');
            this.authService.saveAuthData(res.email, res.role);
            // Redirect to home after successful signup
            setTimeout(() => {
              this.router.navigate(['/Home']);
            }, 1500);
          } else {
            this.toastService.error('Invalid response from server');
          }
        },
        error: (err: any) => {
          this.isLoading = false;
          console.error('Signup failed', err);
          if (err.status === 409) {
            this.toastService.error('User with this email already exists');
          } else {
            this.toastService.error('Signup failed. Please try again.');
          }
        }
      });
    } else {
      this.authForm.markAllAsTouched();
    }
  }

  get email() {
    return this.authForm.get('email');
  }

  get password() {
    return this.authForm.get('password');
  }
}
