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
        map((room) => {
          this.room = room;
          this.preview = room?.imageUrl;
          return room;
        }),
        catchError((err) => {
          const message = 'Could not load room';
          this.messageService.showMessages(new MessageAlert(message, 'error'));
          console.log(message, err);
          return throwError(() => new Error(err));
        })
      )
    );
  }

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

  // Update room function
  updateRoom() {
    console.log('add room');
    if (
      !this.room?.roomType ||
      !this.room?.pricePerNight ||
      !this.room?.capacity ||
      !this.room?.roomNumber ||
      !this.room?.imageUrl 
    ) {
      this.messageService.showMessages(
        new MessageAlert('All room details must be provided.', 'error')
      );
      return;
    }

    // TODO: use bootstrap modal here
    if (!window.confirm('Do you want to add this room?')) {
      return;
    }

    const formData = new FormData();
    formData.append('roomType', this.room?.roomType);
    formData.append('pricePerNight', String(this.room!.pricePerNight));
    formData.append('capacity', String(this.room!.capacity));
    formData.append('roomNumber', String(this.room!.roomNumber));
    formData.append('description', this.room?.description);

    if (this.file) {
      formData.append('imageFile', this.file);
    }

    this.apiService.updateRoom(formData).subscribe({
      next: (res: Response) => {
        if (res['status'] == 200) {
          const message = 'Room updated successfully!!';
          this.messageService.showMessages(
            new MessageAlert(message, 'success')
          );
        }
      },
      error: (err) => {
        this.messageService.showMessages(
          new MessageAlert(
            err?.error?.message || 'Unable to update room.',
            'error'
          )
        );
        return throwError(() => new Error(err));
      },
    });
  }
}
