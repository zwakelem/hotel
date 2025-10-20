import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../service/api';
import { BookingListComponent } from '../booking-list-component/booking-list-component';
import { Booking } from '../../model/booking';
import { EMPTY, Observable } from 'rxjs';
import { LoadingService } from '../../service/loading.service';
import { LoadingComponent } from '../../common/loading/loading.component';
import { MessagesService } from '../../service/messages.service';
import { Messages } from '../../common/messages/messages';

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
      console.log('Please enter the booking confirmation code');
      this.messagesService.showErrors(
        'Please enter the booking confirmation code'
      );
      return;
    }

    // wrap Observable with loading service call to show spinner
    this.bookingDetails$ = this.loading.showLoaderUntilCompleted(
      this.apiService.getBookingByReference(this.confirmationCode)
    );
  }
}
