import { CommonModule } from '@angular/common';
import { Component, AfterViewInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { MessageAlert } from '../../model/messageAlert';
import { Room } from '../../model/room';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';

declare var bootstrap: any;

@Component({
  selector: 'app-edit-room-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-room-component.html',
  styleUrl: './edit-room-component.css',
})
export class EditRoomComponent implements AfterViewInit {
  roomId: string = '';
  room: Room | null = null;
  room$: Observable<Room> = EMPTY;
  roomTypes$: Observable<string[]> = EMPTY;

  file: File | null = null;
  preview: string | null = null;

  private updateModal: any;
  private deleteModal: any;

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

  ngAfterViewInit() {
    this.initializeModals();
  }

  initializeModals() {
    const updateModalElement = document.getElementById('updateConfirmModal');
    const deleteModalElement = document.getElementById('deleteConfirmModal');
    
    if (updateModalElement) {
      this.updateModal = new bootstrap.Modal(updateModalElement);
    }
    if (deleteModalElement) {
      this.deleteModal = new bootstrap.Modal(deleteModalElement);
    }
  }

  showUpdateConfirmModal() {
    if (
      !this.room?.id ||
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
    this.updateModal?.show();
  }

  showDeleteConfirmModal() {
    this.deleteModal?.show();
  }

  confirmUpdate() {
    this.updateModal?.hide();
    this.updateRoom();
  }

  confirmDelete() {
    this.deleteModal?.hide();
    this.deleteRoom();
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
    console.log('update room');
    const formData = new FormData();
    formData.append('id', String(this.room!.id));
    formData.append('roomType', this.room?.roomType || '');
    formData.append('pricePerNight', String(this.room!.pricePerNight));
    formData.append('capacity', String(this.room!.capacity));
    formData.append('roomNumber', String(this.room!.roomNumber));
    formData.append('description', this.room?.description || '');

    if (this.file) {
      formData.append('imageFile', this.file);
    }

    //TODO: ???
    this.apiService.updateRoom(formData).subscribe(
      (res: any) => {
        if (res['status'] == 204) {
          const message = 'Room updated successfully!!';
          this.messageService.showMessages(
            new MessageAlert(message, 'success')
          );
          // this.router.navigate(['/admin/manage-rooms']);
        }
      },
      (err) => {
        this.messageService.showMessages(
          new MessageAlert(
            err?.error?.message || 'Unable to update room.',
            'error'
          )
        );
        return throwError(() => new Error(err));
      }
    );
  }

  deleteRoom() {
    console.log('delete room');
    this.apiService.deleteRoom(this.roomId).subscribe({
      next: (res) => {
        if (res['status'] == 204) {
          const message = 'Room deleted successfully!!';
          this.messageService.showMessages(
            new MessageAlert(message, 'success')
          );
          this.resetForm();
          // this.router.navigate(['/admin/manage-rooms']);
        }
      },
      error: (err) => {
        const message = 'Could not delete room';
        this.messageService.showMessages(new MessageAlert(message, 'error'));
        console.log(message, err);
        return throwError(() => new Error(err));
      },
    });

  }

  resetForm() {
    this.file = null;
    this.preview = null;
    this.room = null;
  }
}
