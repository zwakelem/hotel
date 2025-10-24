import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { MessageAlert } from '../model/messageAlert';

@Injectable()
export class MessagesService {
  private subject = new BehaviorSubject<MessageAlert[]>([]);

  messages$: Observable<MessageAlert[]> = this.subject
    .asObservable()
    .pipe(filter((messages) => messages && messages.length > 0));

  showMessages(...messages: MessageAlert[]) {
    this.subject.next(messages);
  }
}
