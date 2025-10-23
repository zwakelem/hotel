import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { EMPTY, Observable, tap } from 'rxjs';
import { MessagesService } from '../../service/messages.service';

@Component({
  selector: 'app-messages',
  imports: [CommonModule],
  templateUrl: './messages.html',
  styleUrl: './messages.css',
})
export class Messages {

  showMessages = false;
  errors$: Observable<string[]> = EMPTY;
  success$: Observable<string[]> = EMPTY;
  @Input()
  messageType: string = 'error';

  constructor(public messagesService: MessagesService) {}

  ngOnInit() {
    this.errors$ = this.messagesService.errors$.pipe(
      tap(() => {
        this.showMessages = true;
        this.messageType = 'error';
      })
    );

    this.success$ = this.messagesService.success$.pipe(
      tap(() => {
        this.showMessages = true;
        this.messageType = 'success';
      })
    );
  }

  onClose() {
    this.showMessages = false;
  }
}
