import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { catchError, EMPTY, Observable, throwError } from 'rxjs';
import { LoadingComponent } from '../../common/loading/loading.component';
import { Booking } from '../../model/booking';
import { MessageAlert } from '../../model/messageAlert';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';
import { BookingListComponent } from '../booking-list-component/booking-list-component';

@Component({
  selector: 'app-find-booking',
  imports: [CommonModule, FormsModule, BookingListComponent, LoadingComponent],
  templateUrl: './find-booking.html',
  styleUrl: './find-booking.css',
})
export class FindBooking {
  confirmationCode: string = '';
  bookingDetails$: Observable<Booking> = EMPTY;

  constructor(
    private apiService: ApiService,
    private loading: LoadingService,
    private messagesService: MessagesService
  ) {}

  handleSearch() {
    if (!this.confirmationCode.trim()) {
      this.messagesService.showMessages(
        new MessageAlert('Please enter the booking confirmation code', 'error')
      );
      return;
    }

    // wrap Observable with loading service call to show spinner
    this.bookingDetails$ = this.loading.showLoaderUntilCompleted(
      this.apiService.getBookingByReference(this.confirmationCode).pipe(
        catchError((err) => {
          this.messagesService.showMessages(new MessageAlert('Could not find booking', 'error'));
          return throwError(() => new Error(err));
        })
      )
    );
  }
}
