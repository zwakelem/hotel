import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EMPTY, Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { MessageAlert } from '../../model/messageAlert';
import { Room, sortRoomsById } from '../../model/room';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';
import { Roomresult } from '../roomresult/roomresult';
import { Roomsearch } from '../roomsearch/roomsearch';

@Component({
  selector: 'app-rooms',
  imports: [Roomresult, Roomsearch, FormsModule],
  templateUrl: './rooms.html',
  styleUrl: './rooms.css',
})
export class Rooms {
  rooms$: Observable<Room[]> = EMPTY;
  filteredRooms$: Observable<Room[]> = EMPTY;
  selectedRoomType: string = '';

  constructor(
    private apiService: ApiService,
    private loadingService: LoadingService,
    private messageService: MessagesService
  ) {}

  ngOnInit() {
    this.rooms$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getAllRooms().pipe(
        map((rooms) => rooms.sort(sortRoomsById)),
        catchError((err) => {
          const message = 'Could not load rooms';
          this.messageService.showMessages(new MessageAlert(message, 'error'));
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
    this.filteredRooms$ = this.rooms$;
  }

  filterRooms(selectedType: string) {
    if (selectedType) {
      this.filteredRooms$ = this.rooms$.pipe(
        map((rooms) => rooms.filter((room) => room.roomType == selectedType))
      );
    } else {
      this.filteredRooms$ = this.rooms$;
    }
  }

  roomSearchResults(searchResults: Observable<Room[]>) {
    this.filteredRooms$ = searchResults;
  }
}
