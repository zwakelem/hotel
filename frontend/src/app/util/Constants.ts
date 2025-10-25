export class Constants {
  static readonly BASE_URL = 'http://localhost:8080/api';
  // static readonly BASE_URL =
  //   'http://hotel-app-env.eu-west-1.elasticbeanstalk.com/api';
  static readonly ENCRYPTION_KEY = 'dennis-encrypt-key';
  static readonly STRIPE_KEY = 'dennis-encrypt-key';
  static readonly BOOKING_STATUSES = [
    'BOOKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED'
  ];
  static readonly PAYMENT_STATUSES = [
    'PENDING', 'COMPLETED', 'FAILED', 'REFUNDED', 'REVERSED'
  ];
}
