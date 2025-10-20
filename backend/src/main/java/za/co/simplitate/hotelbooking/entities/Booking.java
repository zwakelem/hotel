package za.co.simplitate.hotelbooking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.simplitate.hotelbooking.enums.BookingStatus;
import za.co.simplitate.hotelbooking.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedQuery(name = "Booking.isRoomAvailable", query = """
    SELECT CASE WHEN COUNT(b) = 0 THEN true ELSE false END
    FROM Booking b
    WHERE b.room.id = :roomId
    AND :checkInDate <= b.checkOutDate
    AND :checkOutDate >= b.checkInDate
    AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
    """)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE) // remove bookings if user is deleted
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.REMOVE) // remove bookings if room is deleted
    @JoinColumn(name = "room_id")
    private Room room;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BigDecimal totalPrice;

    private String bookingReference;

    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
}
