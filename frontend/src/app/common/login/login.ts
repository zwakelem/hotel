import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { FormsModule } from '@angular/forms';
import { ApiService } from '../../service/api';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  formData: any = {
    email: '',
    password: '',
  };
  error: any = null;

  constructor(
    private apiService: ApiService,
    private router: Router,
    private MessagesService: MessagesService
  ) {}

  async handleSubmit() {
    if (!this.formData.email || !this.formData.password) {
      this.showError('All fields required!!');
      return;
    }
    this.apiService.loginUser(this.formData).subscribe({
      next: (res: any) => {
        if (res.status === 200) {
          this.apiService.encryptAndSaveToStorage('token', res.token);
          this.apiService.encryptAndSaveToStorage('role', res.role);
          this.router.navigate(['/']);
        }
      },
      error: (err: any) => {
        this.MessagesService.showErrors('Unable to login user!!');
      },
    });
  }

  showError(message: string) {
    this.error = message;
    setTimeout(() => {
      this.error = null;
    }, 4000);
  }
}
