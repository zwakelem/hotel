import { CommonModule } from '@angular/common';
import { AfterViewInit, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { Booking } from '../../model/booking';
import { MessageAlert } from '../../model/messageAlert';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';
import { Constants } from '../../util/Constants';

declare var bootstrap: any;

@Component({
  selector: 'app-update-booking-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './update-booking-component.html',
  styleUrl: './update-booking-component.css',
})
export class UpdateBookingComponent implements AfterViewInit {
  booking: Booking = {} as Booking;
  booking$: Observable<Booking> = EMPTY;
  bookingRef: string = '';
  bookingStatusOptions: string[] = Constants.BOOKING_STATUSES;
  paymentStatusOptions: string[] = Constants.PAYMENT_STATUSES;

  private updateModal: any;
  private deleteModal: any;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute,
    private messagesService: MessagesService,
    private loadingService: LoadingService
  ) {}

  ngOnInit() {
    this.bookingRef = this.route.snapshot.paramMap.get('bookingReference')!;
    this.getBooking();
  }

  ngAfterViewInit() {
    this.initializeModals();
  }

  initializeModals() {
    const updateModalElement = document.getElementById('updateModal');
    const deleteModalElement = document.getElementById('deleteModal');

    if (updateModalElement) {
      this.updateModal = new bootstrap.Modal(updateModalElement);
    }
    if (deleteModalElement) {
      this.deleteModal = new bootstrap.Modal(deleteModalElement);
    }
  }

  updateBooking() {
    console.log(this.booking);
    this.apiService.updateBooking(this.booking).subscribe(
      (res) => {
        if (res.status == 204) {
          const message = 'Booking updated successfully!!';
          this.messagesService.showMessages(
            new MessageAlert(message, 'success')
          );
        }
      },
      (err) => {
        const message = 'Could not update booking!!';
        this.messagesService.showMessages(new MessageAlert(message, 'error'));
      }
    );
  }

  deleteBooking() {
    console.log('delete booking');
    this.apiService.deleteBooking(this.bookingRef).subscribe({
      next: (res) => {
        if (res['status'] == 204) {
          const message = 'Room deleted successfully!!';
          this.messagesService.showMessages(
            new MessageAlert(message, 'success')
          );
        }
      },
      error: (err) => {
        const message = 'Could not delete room';
        this.messagesService.showMessages(new MessageAlert(message, 'error'));
        console.log(message, err);
        return throwError(() => new Error(err));
      },
    });
  }

  getBooking() {
    this.booking$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getBookingByReference(this.bookingRef).pipe(
        map((booking) => {
          this.booking = booking;
          return booking;
        }),
        catchError((err) => {
          const message = 'Could not load booking';
          this.messagesService.showMessages(new MessageAlert(message, 'error'));
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
  }

  showUpdateConfirmModal() {
    this.updateModal?.show();
  }

  showDeleteConfirmModal() {
    this.deleteModal?.show();
  }

  confirmUpdate() {
    this.updateModal?.hide();
    this.updateBooking();
  }

  confirmDelete() {
    this.deleteModal?.hide();
    this.deleteBooking();
  }

  cancel() {
    this.router.navigate(['/admin/manage-bookings']);
  }
}
