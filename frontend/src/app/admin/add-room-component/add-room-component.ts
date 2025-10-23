import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, EMPTY, map, Observable, throwError } from 'rxjs';
import { ApiService } from '../../service/api';
import { LoadingService } from '../../service/loading.service';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-add-room-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-room-component.html',
  styleUrl: './add-room-component.css',
})
export class AddRoomComponent {
  roomDetails = {
    imageUrl: null,
    roomType: '',
    roomNumber: '',
    pricePerNight: '',
    capacity: '',
    description: '',
  };

  roomTypes$: Observable<string[]> = EMPTY;
  newRoomType: string = '';

  file: File | null = null;
  preview: string | null = null;

  error: any = null;
  success: string = '';

  constructor(
    private apiService: ApiService,
    private router: Router,
    private loadingService: LoadingService,
    private messageService: MessagesService
  ) {}

  ngOnInit(): void {
    this.fetchRoomTypes();
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

  // Handle form input changes
  handleChange(event: Event) {
    const { name, value } = <HTMLInputElement>event.target;
    this.roomDetails = { ...this.roomDetails, [name]: value };
  }

  // Handle room type change
  handleRoomTypeChange(event: Event) {
    this.roomDetails.roomType = (<HTMLSelectElement>event.target).value;
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

  // Add room function
  addRoom() {
    console.log('add room');
    if (
      !this.roomDetails.roomType ||
      !this.roomDetails.pricePerNight ||
      !this.roomDetails.capacity ||
      !this.roomDetails.roomNumber
    ) {
      // this.showError('All room details must be provided.');
      return;
    }

    if (!window.confirm('Do you want to add this room?')) {
      return;
    }

    const formData = new FormData();
    formData.append('roomType', this.roomDetails.roomType);
    formData.append('pricePerNight', this.roomDetails.pricePerNight);
    formData.append('capacity', this.roomDetails.capacity);
    formData.append('roomNumber', this.roomDetails.roomNumber);
    formData.append('description', 'this is just a description');
    // formData.append('description', this.roomDetails.description);

    if (this.file) {
      formData.append('imageFile', this.file);
    }

    this.apiService.addRoom(formData).subscribe({
      next: (res: any) => {
        if (res.status === 200) {
          const message = 'Room addded successfully!!';
          this.messageService.showSuccess(message);
          this.router.navigate(['/admin/manage-rooms']);
        }
      },
      error: (err) => {
        this.messageService.showErrors(
          err?.error?.message || 'Unable to make a booking.'
        );
      },
    });

    // this.apiService.addRoom(formData).subscribe({
    //   next: (response: Response) => {
    //     console.log(response);
    //     this.success = 'Room Added successfully.';
    //     setTimeout(() => {
    //       this.success = '';
    //       this.router.navigate(['/admin/manage-rooms']);
    //     }, 5000);
    //   },
    //   error: (error: any) => {
    //     console.log(error);
    //     // this.showError(error?.error?.message || 'Error adding room');
    //   },
    // });
  }
}
