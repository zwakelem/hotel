import { Component, ChangeDetectorRef } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { ApiService } from '../../service/api';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class Navbar {
  constructor(
    private router: Router,
    private apiService: ApiService,
    private cdref: ChangeDetectorRef
  ) {}

  ngAfterContentChecked() {
    this.cdref.detectChanges();
  }

  get isAuthenticated(): boolean {
    return this.apiService.isAuthenticated();
  }

  get isCustomer(): boolean {
    return this.apiService.isCustomer();
  }

  get isAdmin(): boolean {
    return this.apiService.isAdmin();
  }

  handleLogout(): void {
    const isLogout = window.confirm('Are you sure you want to logout?');
    if (isLogout) {
      this.apiService.logout();
      this.router.navigate(['/home']);
    }
  }
}
