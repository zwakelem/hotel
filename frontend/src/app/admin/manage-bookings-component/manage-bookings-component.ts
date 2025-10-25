import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, EMPTY, Observable, throwError } from 'rxjs';
import { Booking } from '../../model/booking';
import { MessageAlert } from '../../model/messageAlert';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-manage-bookings-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-bookings-component.html',
  styleUrl: './manage-bookings-component.css',
})
export class ManageBookingsComponent {
  bookingRef: string = '';
  bookings$: Observable<Booking[]> = EMPTY;

  constructor(
    private messagesService: MessagesService,
    private apiService: ApiService,
    private loading: LoadingService
  ) {}

  OnInit() {
    this.loadAllBookings();
  }

  loadAllBookings() {
    // wrap Observable with loading service call to show spinner
    this.bookings$ = this.loading.showLoaderUntilCompleted(
      this.apiService.getAllBookings().pipe(
        catchError((err) => {
          this.messagesService.showMessages(
            new MessageAlert('Could not find booking', 'error')
          );
          return throwError(() => new Error(err));
        })
      )
    );
  }

  handleSearch() {
    if (!this.bookingRef.trim()) {
      this.messagesService.showMessages(
        new MessageAlert('Please enter the booking confirmation code', 'error')
      );
      return;
    }
  }
}
