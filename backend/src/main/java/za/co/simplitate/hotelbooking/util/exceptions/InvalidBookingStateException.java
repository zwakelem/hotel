package za.co.simplitate.hotelbooking.util.exceptions;

public class InvalidBookingStateException extends RuntimeException {
    public InvalidBookingStateException(String message) {
        super(message);
    }
}
