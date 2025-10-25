import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Booking } from '../../model/booking';
import { MessageAlert } from '../../model/messageAlert';
import { ApiService } from '../../service/api';
import { MessagesService } from '../../service/messages.service';
import { Constants } from '../../util/Constants';

@Component({
  selector: 'app-update-booking-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './update-booking-component.html',
  styleUrl: './update-booking-component.css',
})
export class UpdateBookingComponent {
  // @Input()
  booking: Booking = this.getBooking();
  bookingRef: string = '';
  bookingStatusOptions: string[] = Constants.BOOKING_STATUSES;
  paymentStatusOptions: string[] = Constants.PAYMENT_STATUSES;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private route: ActivatedRoute,
    private messagesService: MessagesService
  ) {}

  OnInit() {
    this.bookingRef = this.route.snapshot.paramMap.get('bookingReference')!;
    // this.getBooking(this.bookingRef);
  }

  updateBooking() {
    console.log(' ***** ');
    this.apiService.updateBooking(this.booking).subscribe(
      (res) => {
        if(res.status == 204) {
          const message = 'Booking updated successfully!!';
          this.messagesService.showMessages(new MessageAlert(message, 'success'));
        }
      },
      (err) => {
        const message = 'Could not update booking!!';
        this.messagesService.showMessages(new MessageAlert(message, 'error'));
      } 
    );
  }

  getBooking(): Booking {
    return {
      id: 16,
      user: {
        id: 4,
        email: 'sim@gmail.com',
        firstName: 'Sim',
        lastName: 'Mgabhi',
        phoneNumber: '0119995008',
        role: 'CUSTOMER',
        createdAt: new Date('2025-08-25'),
        isActive: true,
      },
      room: {
        id: 10,
        roomNumber: 104,
        roomType: 'TRIPLE',
        pricePerNight: 500.0,
        capacity: 6,
        description:
          'Experience comfort and style in our elegant Double Room, perfect for couples or business travelers seeking relaxation and convenience. Enjoy plush bedding, modern amenities, and a serene atmosphere designed to make every stay unforgettable.',
        imageUrl:
          'https://sim-hotel-app.s3.eu-west-1.amazonaws.com/room-images/720455319.jpg',
      },
      paymentStatus: 'COMPLETED',
      checkInDate: new Date('2025-10-25'),
      checkOutDate: new Date('2025-10-27'),
      totalPrice: 1000.0,
      bookingReference: '74fCt4pmPP',
      createdAt: new Date('2025-10-22'),
      bookingStatus: 'BOOKED',
    };
  }
}
