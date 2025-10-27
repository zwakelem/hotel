import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MessageAlert } from '../../model/messageAlert';
import { RegistrationRequest } from '../../model/registrationRequest';
import { ApiService } from '../../service/api';
import { MessagesService } from '../../service/messages.service';
import { Constants } from '../../util/Constants';

@Component({
  selector: 'app-admin-register-component',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-register-component.html',
})
export class AdminRegisterComponent {
  roles: string[] = Constants.ROLES;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private messagesService: MessagesService
  ) {}

  user: RegistrationRequest = {} as RegistrationRequest;

  message: string | null = null;

  handleSubmit() {
    if (
      !this.user.email ||
      !this.user.firstName ||
      !this.user.lastName ||
      !this.user.phoneNumber ||
      !this.user.password ||
      !this.user.role
    ) {
      this.messagesService.showMessages(
        new MessageAlert('All fields are required', 'error')
      );
      return;
    }

    this.apiService.registerUser(this.user).subscribe({
      next: (res) => {
        if(res.status == 200) {
          this.messagesService.showMessages(
            new MessageAlert('User registered!!', 'success')
          );
        }
        this.router.navigate(['/admin']);
      },
      error: (err) => {
        this.messagesService.showMessages(
          new MessageAlert(
            err?.error?.message ||
              err.message ||
              'Unable To Register a user: ' + err,
            'error'
          )
        );
      },
    });
  }
}
