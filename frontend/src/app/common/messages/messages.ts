import { Component } from '@angular/core';
import { MessagesService } from '../../service/messages.service';
import { CommonModule } from '@angular/common';
import { EMPTY, Observable, tap } from 'rxjs';

@Component({
  selector: 'app-messages',
  imports: [CommonModule],
  templateUrl: './messages.html',
  styleUrl: './messages.css',
})
export class Messages {
  //TODO: make it reusable for error or success or warn messages
  showMessages = false;
  errors$: Observable<string[]> = EMPTY;

  constructor(public messagesService: MessagesService) {}

  ngOnInit() {
    this.errors$ = this.messagesService.errors$.pipe(
      tap(() => (this.showMessages = true))
    );
  }

  onClose() {
    this.showMessages = false;
  }
}
