import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

@Injectable()
export class MessagesService {
  private errorsSubject = new BehaviorSubject<string[]>([]);
  private successSubject = new BehaviorSubject<string[]>([]);

  errors$: Observable<string[]> = this.errorsSubject
    .asObservable()
    .pipe(filter((messages) => messages && messages.length > 0));

  success$: Observable<string[]> = this.successSubject
    .asObservable()
    .pipe(filter((messages) => messages && messages.length > 0));

  showErrors(...errors: string[]) {
    this.errorsSubject.next(errors);
  }

  showSuccess(...message: string[]) {
    this.successSubject.next(message);
  }
}
