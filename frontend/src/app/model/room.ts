export interface Room {
  id: number;
  roomNumber: number;
  roomType: string;
  pricePerNight: number;
  capacity: number;
  description: string;
  imageUrl: string;
}

export function sortRoomsById(r1: Room, r2: Room) {
  return r1.id - r2.id;
}
