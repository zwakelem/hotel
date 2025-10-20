import { Component, Input } from '@angular/core';
import { Booking } from '../../model/booking';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-booking-list-component',
  imports: [CommonModule],
  templateUrl: './booking-list-component.html',
  styleUrl: './booking-list-component.css',
})
export class BookingListComponent {
  @Input()
  bookingDetails: Booking | null = null;
}
