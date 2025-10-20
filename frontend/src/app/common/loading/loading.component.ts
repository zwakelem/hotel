import { Component, OnInit } from '@angular/core';
import { LoadingService } from '../../service/loading.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.css'],
  imports: [CommonModule],
})
export class LoadingComponent implements OnInit {
  constructor(public loadingService: LoadingService) {}

  ngOnInit() {}
}
