import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../service/api';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  formData: any = {
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    password: '',
  };

  constructor(
    private apiService: ApiService,
    private router: Router,
    private messagesService: MessagesService
  ) {}

  handleSubmit() {
    if (
      !this.formData.email ||
      !this.formData.firstName ||
      !this.formData.lastName ||
      !this.formData.phoneNumber ||
      !this.formData.password
    ) {
      this.messagesService.showErrors('All fields are required!');
      return;
    }

    this.apiService.registerUser(this.formData).subscribe({
      next: (res: any) => {
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        this.messagesService.showErrors('Unable to register user!!');
      },
    });
  }
}
