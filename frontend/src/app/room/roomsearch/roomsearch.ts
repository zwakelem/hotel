import { Component, EventEmitter, Output } from '@angular/core';

import { FormsModule } from '@angular/forms';
import {
  NgbCalendar,
  NgbDatepickerModule,
  NgbDateStruct,
  NgbModule,
} from '@ng-bootstrap/ng-bootstrap';
import { ApiService } from '../../service/api';
import { Constants } from '../../util/Constants';
import { Room } from '../../model/room';
import { MessagesService } from '../../service/messages.service';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { LoadingService } from '../../service/loading.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-roomsearch',
  imports: [CommonModule, FormsModule, NgbModule, NgbDatepickerModule],
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
    console.log('search - on init');
    console.log(this.getDateSixMonthsFromNow());

    this.minDate = this.calendar.getToday();
    this.maxDate = this.getDateSixMonthsFromNow();

    this.roomTypes$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getAllRoomTypes().pipe(
        map((types) => types),
        catchError((err) => {
          const message = 'Could not load room types';
          this.messageService.showErrors(message);
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
    if (!this.startDate || !this.endDate || !this.roomType) {
      this.messageService.showErrors('Please select all fields');
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
      this.messageService.showErrors('Invalid date format');
      return;
    }

    // Convert the Date objects to 'yyyy-MM-dd' format
    const startDateStr = formattedStartDate.toLocaleDateString('en-CA'); // 'yyyy-MM-dd'
    const endDateStr = formattedEndDate.toLocaleDateString('en-CA'); // 'yyyy-MM-dd'

    console.log('formattedStartDate: ' + startDateStr);
    console.log('formattedEndDate: ' + endDateStr);
    console.log('roomType: ' + this.roomType);

    this.apiService
      .getAvailableRooms(startDateStr, endDateStr, this.roomType)
      .subscribe({
        next: (resp: any) => {
          if (resp.rooms.length === 0) {
            this.messageService.showErrors(
              'Room type not currently available for the selected date'
            );
            return;
          }
          console.log('rooms found');
          console.log(resp.rooms);
          this.searchResults.emit(resp.rooms); // Emit the room data
          this.error = ''; // Clear any previous errors
        },
        error: (error: any) => {
          this.messageService.showErrors(
            error?.error?.message || error.message
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
