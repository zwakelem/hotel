import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  NgbDatepickerModule,
  NgbDateStruct,
  NgbModule,
} from '@ng-bootstrap/ng-bootstrap';
import { EMPTY, Observable } from 'rxjs';
import { Messages } from '../../common/messages/messages';
import { MessageAlert } from '../../model/messageAlert';
import { Room } from '../../model/room';
import { ApiService } from '../../service/api';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-room-details',
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    NgbDatepickerModule,
    Messages,
  ],
  templateUrl: './room-details.html',
  styleUrl: './room-details.css',
  providers: [MessagesService, CurrencyPipe],
})
export class RoomDetails {
  @ViewChild(Messages)
  messagesComponent!: Messages;
  room$: Observable<Room> = EMPTY;
  room: Room | null = null;
  roomId: any = '';
  checkInDate: NgbDateStruct = this.todaysDate();
  checkOutDate: NgbDateStruct = this.getDateOneMonthsFromNow();
  selectedCheckInDate?: Date;
  selectedCheckOutDate?: Date;
  selectedCheckIn?: string;
  selectedCheckOut?: string;

  totalPrice: number = 0;
  totalDaysToStay: number = 0;
  showDatePicker: boolean = false;
  showBookingPreview: boolean = false;
  message: any = null;

  minDate: NgbDateStruct = this.todaysDate(); // Current date
  maxDate: NgbDateStruct = this.getDateSixMonthsFromNow();

  constructor(
    private apiService: ApiService,
    private route: ActivatedRoute,
    private router: Router,
    private messagesService: MessagesService,
    private currencyPipe: CurrencyPipe
  ) {}

  ngOnInit() {
    console.log('RoomDetails - onInit');
    this.roomId = this.route.snapshot.paramMap.get('roomId');
    this.room$ = this.apiService.getRoomById(this.roomId);
    this.room$.subscribe((res) => {
      this.room = res;
    });
  }

  calculateTotalPrice(): number {
    if (!this.checkInDate || !this.checkOutDate) {
      return 0;
    }

    const checkIn = this.parseDate(this.checkInDate);
    const checkOut = this.parseDate(this.checkOutDate);

    if (isNaN(checkIn.getTime()) || isNaN(checkOut.getTime())) {
      this.messagesService.showMessages(new MessageAlert('Invalid date selected!!', 'error'));
      return 0;
    }

    const oneDay = 24 * 60 * 60 * 1000;
    const totalDays = Math.round(
      Math.abs((checkOut.getTime() - checkIn.getTime()) / oneDay)
    );
    this.totalDaysToStay = totalDays;
    return this.room ? this.room.pricePerNight * totalDays : 0;
  }

  proceedWithBooking(): void {
    this.onSelectedDate();

    this.totalPrice = this.calculateTotalPrice();
    this.showBookingPreview = true;
  }

  acceptBooking(): void {
    if (!this.room) return;

    const formarttedCheckInDate = this.parseDate(this.checkInDate);
    const formarttedCheckOutDate = this.parseDate(this.checkOutDate);

    const bookingRequest = {
      checkInDate: formarttedCheckInDate.toISOString().slice(0, 10),
      checkOutDate: formarttedCheckOutDate.toISOString().slice(0, 10),
      roomId: this.roomId,
    };

    this.apiService.bookRoom(bookingRequest).subscribe({
      next: (res: any) => {
        if (res.status === 200) {
          this.message =
            'Your booking was successful. A payment link will be emailed to you!!';
        }
      },
      error: (err) => {
        this.messagesService.showMessages(new MessageAlert(
          err?.error?.message || 'Unable to make a booking.', 'error')
        );
      },
    });
  }

  cancelBookingPreview(): void {
    this.showBookingPreview = false;
  }

  get isLoading(): boolean {
    return !this.room;
  }

  parseDate(date: NgbDateStruct): Date {
    return new Date(date.year, date.month - 1, date.day);
  }

  todaysDate(): NgbDateStruct {
    return {
      year: new Date().getFullYear(),
      month: new Date().getMonth() + 1, // Add 1 because native Date.getMonth() is 0-indexed
      day: new Date().getDate(),
    };
  }

  getDateSixMonthsFromNow(): NgbDateStruct {
    const currentDate = new Date(); // Get the current date and time
    currentDate.setMonth(currentDate.getMonth() + 6); // Add 6 months to the current month
    return {
      year: currentDate.getFullYear(),
      month: currentDate.getMonth() + 1, // Add 1 because native Date.getMonth() is 0-indexed
      day: currentDate.getDate(),
    };
  }

  getDateOneMonthsFromNow(): NgbDateStruct {
    const currentDate = new Date(); // Get the current date and time
    currentDate.setMonth(currentDate.getMonth() + 1); // Add 1 months to the current month
    return {
      year: currentDate.getFullYear(),
      month: currentDate.getMonth() + 1, // Add 1 because native Date.getMonth() is 0-indexed
      day: currentDate.getDate(),
    };
  }

  onSelectedDate(): void {
    this.messagesComponent.showMessages = false;

    if (this.checkInDate && this.checkOutDate) {
      this.selectedCheckInDate = this.parseDate(this.checkInDate);
      this.selectedCheckOutDate = this.parseDate(this.checkOutDate);

      this.selectedCheckIn = this.parseDate(this.checkInDate)
        .toISOString()
        .slice(0, 10);
      this.selectedCheckOut = this.parseDate(this.checkOutDate)
        .toISOString()
        .slice(0, 10);

      if (this.selectedCheckOutDate <= this.selectedCheckInDate) {
        this.messagesService.showMessages(new MessageAlert(
          'Check-out date must be after check-in date', 'error')
        );
        return;
      }
    } else {
      this.messagesService.showMessages(new MessageAlert(
        'Check-out and check-in dates are required!!', 'error')
      );
    }
  }

  get formattedTotalPrice(): string {
    return this.currencyPipe.transform(this.totalPrice, 'ZAR') || '';
  }
}
