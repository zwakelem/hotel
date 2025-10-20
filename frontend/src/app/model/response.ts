import { Booking } from './booking';
import { Payment } from './payment';
import { Room } from './room';
import { User } from './user';

export interface Response {
  status: number;
  message: string;
  token: string;
  role: string;
  active: boolean;
  expirationTime: string;
  user: User;
  users: User[];
  booking: Booking;
  bookings: Booking[];
  room: Room;
  rooms: Room[];
  payment: Payment;
  payments: Payment[];
  timestamp: Date;
}
