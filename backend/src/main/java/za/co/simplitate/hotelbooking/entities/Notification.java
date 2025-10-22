package za.co.simplitate.hotelbooking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import za.co.simplitate.hotelbooking.util.enums.NotificationType;

import java.time.LocalDateTime;

@Entity
@Table(name="notifications")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String body;

    @NotBlank(message = "Recipient is required")
    private String recipient;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    private String bookingReference;

    private LocalDateTime createdAt = LocalDateTime.now();
}
