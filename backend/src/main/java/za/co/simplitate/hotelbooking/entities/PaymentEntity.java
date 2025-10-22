package za.co.simplitate.hotelbooking.entities;

import jakarta.persistence.*;
import lombok.*;
import za.co.simplitate.hotelbooking.util.enums.PaymentGateway;
import za.co.simplitate.hotelbooking.util.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transactionId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentGateway paymentGateway;

    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String bookingReference;

    private String failureReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
