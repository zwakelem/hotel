import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  loadStripe,
  Stripe,
  StripeCardElement,
  StripeElements,
} from '@stripe/stripe-js';
import { ApiService } from '../../service/api';
import { ActivatedRoute, Router } from '@angular/router';
import { Constants } from '../../util/Constants';
import { MessagesService } from '../../service/messages.service';

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
        this.messagesService.showErrors(
          'Failed to fetch transaction unique secret!!'
        );
      },
    });
  }

  ///This is the method to call when a user click on pay now after he has filled his card details
  async handleSubmit(event: Event) {
    event.preventDefault();
    console.log('PAY Button was clicked');

    if (
      !this.stripe ||
      !this.elements ||
      !this.clientSecret ||
      this.processing
    ) {
      this.messagesService.showErrors(
        'Please fill in your card details properly'
      );
      return;
    }

    this.processing = true;

    const { error, paymentIntent } = await this.stripe.confirmCardPayment(
      this.clientSecret,
      {
        payment_method: {
          card: this.cardElement!,
        },
      }
    );

    if (paymentIntent && paymentIntent.status === 'succeeded') {
      this.processing = false;
      console.log('Payment intend id is: ' + paymentIntent.id);
      this.handleUpdateBookingPayment('succeeded', paymentIntent.id); // update the boking status in the backend and send email to the user of the status
      this.router.navigate([`/payment-success/${this.bookingReference}`]);
    } else if (error) {
      console.log('PAYMENT ERROR: ' + error);
      this.processing = false;
      this.handleUpdateBookingPayment('failed', '', error.message); // update the boking status in the backend and send email to the user of the status
      this.messagesService.showErrors('PAYMENT ERROR');
    } else {
      this.messagesService.showErrors(
        'Unable to process transaction at the moment. Please Try Again!'
      );
    }
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
        this.messagesService.showErrors(
          err?.error?.message || err?.message || 'Error updating payment status'
        );
        console.error(err);
      },
    });
  }
}
