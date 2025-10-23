import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { Room, sortRoomsById } from '../../model/room';
import { Roomresult } from '../../room/roomresult/roomresult';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-manage-rooms-component',
  imports: [CommonModule, FormsModule, Roomresult],
  templateUrl: './manage-rooms-component.html',
  styleUrl: './manage-rooms-component.css',
})
export class ManageRoomsComponent {

  rooms$: Observable<Room[]> = EMPTY;
  filteredRooms$: Observable<Room[]> = EMPTY;
  roomTypes$: Observable<string[]> = EMPTY;
  selectedRoomType: string = '';

  constructor(
    private apiService: ApiService,
    private router: Router,
    private loadingService: LoadingService,
    private messageService: MessagesService
  ) {}

  ngOnInit(): void {
    this.fetchRooms();
    this.fetchRoomTypes();
  }

  // Fetch all rooms from the API
  fetchRooms() {
    this.rooms$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getAllRooms().pipe(
        map((rooms) => rooms.sort(sortRoomsById)),
        catchError((err) => {
          const message = 'Could not load rooms';
          this.messageService.showErrors(message);
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
    this.filteredRooms$ = this.rooms$;
  }

  // Fetch room types from the API
  fetchRoomTypes() {
    this.roomTypes$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getAllRoomTypes().pipe(
        map((types) => types),
        catchError((err) => {
          const message = 'Could not load rooms';
          this.messageService.showErrors(message);
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
  }

  // Handle changes to room type filter
  handleRoomTypeChange() {
    console.log('handleRoomTypeChange = ' + this.selectedRoomType);
    if (this.selectedRoomType) {
      this.filteredRooms$ = this.rooms$.pipe(
        map((rooms) =>
          rooms.filter((room) => room.roomType == this.selectedRoomType)
        )
      );
    } else {
      this.filteredRooms$ = this.rooms$;
    }
  }

  // Navigate to Add Room page
  addRoom() {
    this.router.navigate(['/admin/add-room']);
  }
}
