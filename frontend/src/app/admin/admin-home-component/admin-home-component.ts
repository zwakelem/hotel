import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { MessageAlert } from '../../model/messageAlert';
import { User } from '../../model/user';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-admin-home-component',
  imports: [CommonModule],
  templateUrl: './admin-home-component.html',
  styleUrl: './admin-home-component.css',
})
export class AdminHomeComponent {

  adminUser$: Observable<User> = EMPTY;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private loadingService: LoadingService,
    private messageService: MessagesService
  ) {}

  ngOnInit(): void {
    this.getAdminDetails();
  }

  getAdminDetails() {
    this.adminUser$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getUserProfile().pipe(
        map((user: User) => user),
        catchError((err) => {
          const message = 'Could not retrieve admin user details';
          this.messageService.showMessages(new MessageAlert(message, 'error'));
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
  }


  // Navigate to Manage Rooms
  navigateToManageRooms(): void {
    this.router.navigate(['/admin/manage-rooms']);
  }

  // Navigate to Manage Bookings
  navigateToManageBookings(): void {
    this.router.navigate(['/admin/manage-bookings']);
  }
}
