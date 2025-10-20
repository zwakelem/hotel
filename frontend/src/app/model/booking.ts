import { Room } from './room';
import { User } from './user';

export interface Booking {
  id: number;
  user: User;
  room: Room;
  paymentStatus: string;
  checkInDate: Date;
  checkOutDate: Date;
  totalPrice: number;
  bookingReference: string;
  createdAt: Date;
  bookingStatus: string;
}

export function sortBookingsById(b1: Booking, b2: Booking) {
  return b1.id - b2.id;
}
