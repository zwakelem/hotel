import { User } from './user';

export interface Payment {
  id: number;
  transactionId: string;
  amount: number;
  paymentGateway: string;
  paymentDate: Date;
  paymentStatus: string;
  bookingReference: string;
  failureReason: string;
  user: User;
}
