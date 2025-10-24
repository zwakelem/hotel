import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { EMPTY, Observable, tap } from 'rxjs';
import { MessageAlert } from '../../model/messageAlert';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-messages',
  imports: [CommonModule],
  templateUrl: './messages.html',
  styleUrl: './messages.css',
})
export class Messages {
  //TODO: make it reusable for error or success or warn messages
  showMessages = false;
  messages$: Observable<MessageAlert[]> = EMPTY;

  constructor(public messagesService: MessagesService) {}

  ngOnInit() {
    this.messages$ = this.messagesService.messages$.pipe(
      tap(() => (this.showMessages = true))
    );
  }

  onClose() {
    this.showMessages = false;
  }
}
