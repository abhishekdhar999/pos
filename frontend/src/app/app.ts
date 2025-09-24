import { Component, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Navbar } from './components/navbar/navbar';
import { CommonModule } from '@angular/common';
import { ToastContainerComponent } from './components/errormodel/toast-container.component';
import { AuthService } from './features/auth/Services/auth.service';
interface ngOnInit {
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule,
    ToastContainerComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements ngOnInit{
  protected readonly title = signal('frontend');




  constructor(private http: HttpClient, private router: Router,private auth: AuthService) {}

  ngOnInit() {
     this.auth.checkSession();
  }
}
