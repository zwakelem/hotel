import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {
  loadStripe,
  Stripe,
  StripeCardElement,
  StripeElements,
} from '@stripe/stripe-js';
import { MessageAlert } from '../../model/messageAlert';
import { ApiService } from '../../service/api';
import { MessagesService } from '../../service/messages.service';
import { Constants } from '../../util/Constants';

@Component({
  selector: 'app-payment-component',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './payment-component.html',
  styleUrl: './payment-component.css',
})
export class PaymentComponent {
  stripe: Stripe | null = null;
  elements: StripeElements | null = null;
  cardElement: StripeCardElement | null = null;

  clientSecret: any = null; //unique transaction id
  error: any = null;
  processing: boolean = false;

  bookingReference: string | null = null;
  amount: number | null = null;

  constructor(
    private apiService: ApiService,
    private route: ActivatedRoute,
    private router: Router,
    private messagesService: MessagesService
  ) {}

  async ngOnInit() {
    this.bookingReference =
      this.route.snapshot.paramMap.get('bookingReference');
    this.amount = parseFloat(this.route.snapshot.paramMap.get('amount') || '0');

    this.stripe = await loadStripe(Constants.STRIPE_KEY);

    if (this.stripe) {
      this.elements = this.stripe.elements();
      this.cardElement = this.elements.create('card');
      this.cardElement.mount('#card-element');
    }

    this.fetchClientSecrete();
  }

  fetchClientSecrete(): void {
    const paymentData = {
      bookingReference: this.bookingReference,
      amount: this.amount,
    };

    this.apiService.proceedForPayment(paymentData).subscribe({
      next: (res: any) => {
        this.clientSecret = res.transactionId;
        console.log(
          'Transactio  ID or CLient Secrete is: ' + res.transactionId
        );
      },
      error: (err: any) => {
        this.messagesService.showMessages(new MessageAlert(
          'Failed to fetch transaction unique secret!!', 'error')
        );
      },
    });
  }

  // This is the method to call when a user click on pay now after he has filled his card details
  async handleSubmit(event: Event) {
    event.preventDefault();
    console.log('PAY Button was clicked');

    this.processing = true;
    this.handleUpdateBookingPayment('succeeded', 'payment-intent-id');
    this.processing = false;
    this.router.navigate(['/payment-success', this.bookingReference]);
  }

  handleUpdateBookingPayment(
    paymentStatus: string,
    transactionId: string = '',
    failureReason: string = ''
  ) {
    console.log('INSIDE handlePaymentStatus()');
    if (!this.bookingReference || !this.amount) return;

    console.log('BOOKING REFERENCE: ' + this.bookingReference);
    console.log('BOOKING AMOUNT IS: ' + this.amount);

    console.log('Payment status is: ' + paymentStatus);
    console.log('transactionId IS: ' + transactionId);
    console.log('failureReason IS: ' + failureReason);

    const paymentData = {
      bookingReference: this.bookingReference,
      amount: this.amount,
      transactionId,
      success: paymentStatus === 'succeeded',
      failureReason,
    };

    this.apiService.updateBookingPayment(paymentData).subscribe({
      next: (res: any) => {
        console.log(res);
      },
      error: (err) => {
        this.messagesService.showMessages(new MessageAlert(
          err?.error?.message || err?.message || 'Error updating payment status', 'error')
        );
        console.error(err);
      },
    });
  }
}
