import { Component, EventEmitter, Output } from '@angular/core';

import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  NgbCalendar,
  NgbDatepickerModule,
  NgbDateStruct,
  NgbModule,
} from '@ng-bootstrap/ng-bootstrap';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { Messages } from '../../common/messages/messages';
import { MessageAlert } from '../../model/messageAlert';
import { Room } from '../../model/room';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-roomsearch',
  imports: [CommonModule, FormsModule, NgbModule, NgbDatepickerModule, Messages],
  templateUrl: './roomsearch.html',
  styleUrl: './roomsearch.css',
})
export class Roomsearch {
  @Output() searchResults = new EventEmitter<Room[]>(); // Emit the results
  @Output() filterByTypesEvent = new EventEmitter<string>(); // Emit the results

  startDate: NgbDateStruct | null = null;
  endDate: NgbDateStruct | null = null;
  roomType: string = ''; // Selected room type
  roomTypes$: Observable<string[]> = EMPTY;

  error: any = null;

  minDate: NgbDateStruct = {
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 1, // Add 1 because native Date.getMonth() is 0-indexed
    day: new Date().getDate(),
  }; // Current date

  maxDate: NgbDateStruct = this.getDateSixMonthsFromNow();

  constructor(
    private apiService: ApiService,
    private calendar: NgbCalendar,
    private messageService: MessagesService,
    private loadingService: LoadingService
  ) {}

  ngOnInit(): void {
    this.minDate = this.calendar.getToday();
    this.maxDate = this.getDateSixMonthsFromNow();

    this.roomTypes$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getAllRoomTypes().pipe(
        map((types) => types),
        catchError((err) => {
          const message = 'Could not load room types';
          this.messageService.showMessages(new MessageAlert(message, 'error'));
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
  }

  filterByTypes() {
    this.filterByTypesEvent.emit(this.roomType);
  }

  handleSearch() {
    if (!this.startDate || !this.endDate) {
      this.messageService.showMessages(new MessageAlert('Please select all fields', 'error'));
      return;
    }

    // Convert startDate and endDate from string to Date
    const formattedStartDate = this.parseDate(this.startDate);
    const formattedEndDate = this.parseDate(this.endDate);

    // Check if the dates are valid
    if (
      isNaN(formattedStartDate.getTime()) ||
      isNaN(formattedEndDate.getTime())
    ) {
      this.messageService.showMessages(new MessageAlert('Invalid date format', 'error'));
      return;
    }

    // Check is end date is after start date
    if (formattedEndDate <= formattedStartDate) {
      this.messageService.showMessages(
        new MessageAlert('Check-out date must be after check-in date', 'error')
      );
      return;
    }

    // Convert the Date objects to 'yyyy-MM-dd' format
    const startDateStr = formattedStartDate.toLocaleDateString('en-CA'); // 'yyyy-MM-dd'
    const endDateStr = formattedEndDate.toLocaleDateString('en-CA'); // 'yyyy-MM-dd'

    this.apiService
      .getAvailableRooms(startDateStr, endDateStr, this.roomType)
      .subscribe({
        next: (resp: any) => {
          if (resp.length === 0) {
            this.messageService.showMessages(
              new MessageAlert('Room type not currently available for the selected date', 'error')
            );
            return;
          }
          console.log('rooms found');
          console.log(resp);
          this.searchResults.emit(resp); // Emit the room data
          this.error = ''; // Clear any previous errors
        },
        error: (error: any) => {
          this.messageService.showMessages(
            new MessageAlert(error?.error?.message || error.message, 'error')
          );
        },
      });
  }

  parseDate(date: NgbDateStruct): Date {
    return new Date(date.year, date.month - 1, date.day);
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
