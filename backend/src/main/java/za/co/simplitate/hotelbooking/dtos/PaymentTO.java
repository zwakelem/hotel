package za.co.simplitate.hotelbooking.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.enums.PaymentGateway;
import za.co.simplitate.hotelbooking.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentTO(
        Long id,
        String transactionId,
        BigDecimal amount,
        PaymentGateway paymentGateway,
        LocalDateTime paymentDate,
        PaymentStatus paymentStatus,
        String bookingReference,
        String failureReason,
        User user
) { }
