import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './common/navbar/navbar';
import { Footer } from './common/footer/footer';
import { LoadingService } from './service/loading.service';
import { MessagesService } from './service/messages.service';
import { Messages } from './common/messages/messages';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Footer, Messages],
  templateUrl: './app.html',
  styleUrl: './app.css',
  providers: [LoadingService, MessagesService],
})
export class App {
  title = 'Hotel-Frontend';
}
