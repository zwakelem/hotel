package za.co.simplitate.hotelbooking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.simplitate.hotelbooking.enums.PaymentGateway;
import za.co.simplitate.hotelbooking.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="payments")
@Data
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
