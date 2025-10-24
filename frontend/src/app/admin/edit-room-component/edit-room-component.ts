import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { MessageAlert } from '../../model/messageAlert';
import { Room } from '../../model/room';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-edit-room-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-room-component.html',
  styleUrl: './edit-room-component.css',
})
export class EditRoomComponent {
  roomId: string = '';
  room: Room | null = null;
  room$: Observable<Room> = EMPTY;
  roomTypes$: Observable<string[]> = EMPTY;

  file: File | null = null;
  preview: string | null = null;

  constructor(
    private messageService: MessagesService,
    private route: ActivatedRoute,
    private router: Router,
    private apiService: ApiService,
    private loadingService: LoadingService
  ) {}

  ngOnInit() {
    this.roomId = this.route.snapshot.paramMap.get('id')!;
    this.fetchRoomById();
    this.fetchRoomTypes();
  }

  // Fetch room types from the API
  fetchRoomTypes() {
    this.roomTypes$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getAllRoomTypes().pipe(
        map((types) => types),
        catchError((err) => {
          const message = 'Could not load rooms';
          this.messageService.showMessages(new MessageAlert(message, 'error'));
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
  }

  fetchRoomById() {
    this.room$ = this.loadingService.showLoaderUntilCompleted(
      this.apiService.getRoomById(this.roomId).pipe(
        map((rooms) => rooms),
        catchError((err) => {
          const message = 'Could not load rooms';
          this.messageService.showMessages(new MessageAlert(message, 'error'));
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
  }

  updateRoom() {}

  // Handle file input change (image upload)
  handleFileChange(event: Event) {
    const input = <HTMLInputElement>event.target;
    const selectedFile = input.files ? input.files[0] : null;
    if (selectedFile) {
      this.file = selectedFile;
      this.preview = URL.createObjectURL(selectedFile);
    } else {
      this.file = null;
      this.preview = null;
    }
  }
}
