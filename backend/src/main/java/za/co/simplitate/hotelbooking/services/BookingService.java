package za.co.simplitate.hotelbooking.services;

import za.co.simplitate.hotelbooking.dtos.BookingRequestTO;
import za.co.simplitate.hotelbooking.dtos.BookingTO;
import za.co.simplitate.hotelbooking.dtos.Response;

public interface BookingService {

    Response getAllBookings();
    Response createBooking(BookingRequestTO bookingRequestTO);
    Response findBookingByReference(String ref);
    Response updateBooking(BookingTO bookingTO);
}
