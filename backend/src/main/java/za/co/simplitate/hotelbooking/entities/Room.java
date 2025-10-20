package za.co.simplitate.hotelbooking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.simplitate.hotelbooking.enums.RoomType;

import java.math.BigDecimal;

@Entity
@Table(name="rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedQuery(name = "Room.findAvailableRooms", query = """
    SELECT r
    FROM Room r
    WHERE r.roomType = :roomType
    """)
@NamedQuery(name = "Room.findByDescription", query = """
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
