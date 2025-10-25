import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { Booking, sortBookingsById } from '../../model/booking';
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
  allBookings$: Observable<Booking[]> = EMPTY;
  filteredBookings$: Observable<Booking[]> = EMPTY;

  constructor(
    private messagesService: MessagesService,
    private apiService: ApiService,
    private loading: LoadingService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadAllBookings();
  }

  loadAllBookings() {
    // wrap Observable with loading service call to show spinner
    this.allBookings$ = this.loading.showLoaderUntilCompleted(
      this.apiService.getAllBookings().pipe(
        map((bookings) => bookings.sort(sortBookingsById)),
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
    console.log(this.bookingRef);
    if (this.bookingRef) {
      this.filteredBookings$ = this.allBookings$.pipe(
        map((bookings) =>
          bookings.filter((booking) =>
            booking.bookingReference.includes(this.bookingRef)
          )
        )
      );
    } else {
      this.filteredBookings$ = this.allBookings$;
    }
  }

  manageBooking(bookingReference: string) {
    this.router.navigate([`admin/update-booking/${bookingReference}`]);
  }
}
