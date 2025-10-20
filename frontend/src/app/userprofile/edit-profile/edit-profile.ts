import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
} from '@angular/core';
import { Router } from '@angular/router';
import { ApiService } from '../../service/api';
import { User } from '../../model/user';
import { FormsModule } from '@angular/forms';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-edit-profile',
  imports: [FormsModule],
  templateUrl: './edit-profile.html',
  styleUrls: ['./edit-profile.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditProfile {
  user: User = {} as User;
  error: any = null;
  userExists: boolean = false;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private cdref: ChangeDetectorRef,
    private messagesService: MessagesService
  ) {}

  ngOnInit(): void {
    this.apiService.getUserProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.cdref.markForCheck();
      },
      error: (err) => {
        this.showError(err?.error?.message || 'Error loading user profile');
      },
    });
  }

  showError(message: string) {
    this.error = message;
    setTimeout(() => {
      this.error = null;
    }, 4000);
  }

  updateProfile(): void {
    console.log('updateProfile');
    this.apiService.updateProfile(this.user).subscribe({
      next: (response) => {
        this.user = response;
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        this.messagesService.showErrors('Error while updating user profile!!');
      },
    });
  }

  handleDeleteProfile(): void {
    if (!window.confirm('Are you sure you want to delete your account?')) {
      return;
    }

    this.apiService.deleteAccount().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.showError(err?.error?.message || 'Error deleting account');
      },
    });
  }

  handleCancel() {
    this.router.navigate(['/profile']);
  }
}
