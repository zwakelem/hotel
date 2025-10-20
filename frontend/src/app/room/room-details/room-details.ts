import { Component } from '@angular/core';
import { ApiService } from '../../service/api';
import { ActivatedRoute, Router } from '@angular/router';
import { Room } from '../../model/room';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EMPTY, Observable } from 'rxjs';
import {
  NgbDatepickerModule,
  NgbDateStruct,
  NgbModule,
} from '@ng-bootstrap/ng-bootstrap';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-room-details',
  imports: [CommonModule, FormsModule, NgbModule, NgbDatepickerModule],
  templateUrl: './room-details.html',
  styleUrl: './room-details.css',
})
export class RoomDetails {
  room$: Observable<Room> = EMPTY;
  room: Room | null = null;
  roomId: any = '';
  checkInDate: NgbDateStruct = this.todaysDate();
  checkOutDate: NgbDateStruct = this.todaysDate();
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
    private messagesService: MessagesService
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
      this.messagesService.showErrors('Invalid date selected!!');
      return 0;
    }

    const oneDay = 24 * 60 * 60 * 1000;
    const totalDays = Math.round(
      Math.abs((checkOut.getTime() - checkIn.getTime()) / oneDay)
    );
    this.totalDaysToStay = totalDays;
    return 0;
    // return this.room$ ? this.room$.pricePerNight * totalDays : 0;
  }

  handleConfirmation(): void {
    if (!this.checkInDate || !this.checkOutDate) {
      this.messagesService.showErrors(
        'Please select both check-in and check-out dates!!'
      );
      return;
    }

    this.totalPrice = this.calculateTotalPrice();
    this.showBookingPreview = true;
  }

  acceptBooking(): void {
    if (!this.room) return;

    const formarttedCheckInDate = this.parseDate(this.checkInDate);
    const formarttedCheckOutDate = this.parseDate(this.checkOutDate);

    const booking = {
      checkInDate: formarttedCheckInDate,
      checkOutDate: formarttedCheckOutDate,
      id: this.roomId,
    };

    this.apiService.bookRoom(booking).subscribe({
      next: (res: any) => {
        if (res.status === 200) {
          this.message =
            'Your booking was successful. A payment link will be emailed to you!!';
          setTimeout(() => {
            this.message = null;
            this.router.navigate(['/rooms']);
          }, 8000);
        }
      },
      error: (err) => {
        this.messagesService.showErrors(
          err?.error?.message || 'Unable to make a booking.'
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
}
