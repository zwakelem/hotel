package za.co.simplitate.hotelbooking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.*;
import za.co.simplitate.hotelbooking.util.enums.RoomType;

import java.math.BigDecimal;

@Entity
@Table(name="rooms")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedQuery(name = "Room.findAvailableRooms", query = """
    SELECT r FROM Room r
        WHERE r.id NOT IN (
                SELECT b.room.id
                FROM Booking b
                WHERE :checkInDate <= b.checkOutDate
                AND :checkOutDate >= b.checkInDate
                AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
            )
            AND (:roomType IS NULL OR r.roomType = :roomType)
    """)
@NamedQuery(name = "Room.findAvailableRoomsByDates", query = """
    SELECT r FROM Room r
        WHERE r.id NOT IN (
                SELECT b.room.id
                FROM Booking b
                WHERE :checkInDate <= b.checkOutDate
                AND :checkOutDate >= b.checkInDate
                AND b.bookingStatus IN ('BOOKED', 'CHECKED_IN')
            )
    """)
@NamedQuery(name = "Room.findByDescription", query = """
    SELECT r
    FROM Room r
    WHERE r.description LIKE CONCAT('%', :searchParam, '%')
    """)
@NamedQuery(name = "Room.findByBookings", query = """
    SELECT r
    FROM Room r
    WHERE r.description LIKE CONCAT('%', :searchParam, '%')
    """)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Room number must be at least 1")
    @Column(unique = true)
    private Integer roomNumber;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @DecimalMin(value = "0.1", message = "Price per night is required")
    private BigDecimal pricePerNight;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String description;

    private String imageUrl;

}
