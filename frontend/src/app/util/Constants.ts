export class Constants {
  static readonly BASE_URL: string = Constants.resolveBaseUrl();
  static readonly ENCRYPTION_KEY = 'dennis-encrypt-key';
  static readonly STRIPE_KEY = 'dennis-encrypt-key';
  static readonly BOOKING_STATUSES = [
    'BOOKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED'
  ];
  static readonly PAYMENT_STATUSES = [
    'PENDING', 'COMPLETED', 'FAILED', 'REFUNDED', 'REVERSED'
  ];
  static readonly ROLES = ['ADMIN', 'CUSTOMER'];

  private static resolveBaseUrl(): string {
    // server-side safety
    if (typeof window === 'undefined' || !window.location) {
      return 'http://localhost:8080/api';
    }

    const host = window.location.hostname;
    const origin = window.location.origin;

    // local dev
    if (host === 'localhost' || host === '127.0.0.1') {
      return 'http://localhost:8080/api';
    }

    // known prod host (example)
    if (host.includes('elasticbeanstalk') || host.includes('hotel-app')) {
      // use origin so API is served from same host
      return 'http://hotel-app-env.eu-west-1.elasticbeanstalk.com/api';
    }

    // default fallback to origin + /api
    return 'http://hotel-app-env.eu-west-1.elasticbeanstalk.com/api';
  }
}
