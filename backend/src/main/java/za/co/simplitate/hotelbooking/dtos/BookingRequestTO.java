package za.co.simplitate.hotelbooking.dtos;

import java.time.LocalDate;

public record BookingRequestTO (
    LocalDate checkInDate,
    LocalDate checkOutDate,
    Long roomId
) {}
