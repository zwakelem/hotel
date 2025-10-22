package za.co.simplitate.hotelbooking.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="booking_reference")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String referenceNumber;
}
