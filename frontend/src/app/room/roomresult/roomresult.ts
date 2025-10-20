import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { EMPTY, Observable } from 'rxjs';
import { LoadingComponent } from '../../common/loading/loading.component';
import { Room } from '../../model/room';
import { ApiService } from '../../service/api';

@Component({
  selector: 'app-roomresult',
  imports: [CommonModule, LoadingComponent],
  templateUrl: './roomresult.html',
  styleUrl: './roomresult.css',
})
export class Roomresult {
  @Input() roomSearchResults$: Observable<Room[]> = EMPTY;
  isAdmin: boolean;

  constructor(private router: Router, private apiService: ApiService) {
    this.isAdmin = this.apiService.isAdmin();
  }

  ngOnInit(): void {}

  navigateToEditRoom(roomId: number) {
    this.router.navigate([`/admin/edit-room/${roomId}`]);
  }

  navigateToRoomDetails(roomId: number) {
    console.log('room id' + roomId);
    this.router.navigate([`/rooms-details/${roomId}`]);
  }
}
