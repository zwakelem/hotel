package za.co.simplitate.hotelbooking.util;

import za.co.simplitate.hotelbooking.util.exceptions.InvalidBookingStateException;

import java.time.LocalDate;

public class CommonUtil {

    public static void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {

        if(checkInDate != null && checkOutDate != null) {
            if(checkInDate.isBefore(LocalDate.now())) {
                throw new InvalidBookingStateException("Check IN date must be before today");
            }

            if(checkOutDate.isBefore(checkInDate)) {
                throw new InvalidBookingStateException("Check OUT date must be before check IN date");
            }

            if(checkInDate.isEqual(checkOutDate)) {
                throw new InvalidBookingStateException("Check IN date cannot be equal to check OUT date");
            }
        }
    }
}
