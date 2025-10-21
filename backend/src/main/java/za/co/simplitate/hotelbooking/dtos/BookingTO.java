package za.co.simplitate.hotelbooking.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import za.co.simplitate.hotelbooking.entities.Room;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.util.enums.BookingStatus;
import za.co.simplitate.hotelbooking.util.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingTO(
        Long id,
        User user,
        Room room,
        PaymentStatus paymentStatus,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalPrice,
        String bookingReference,
        LocalDate createdAt,
        BookingStatus bookingStatus
) {
}
