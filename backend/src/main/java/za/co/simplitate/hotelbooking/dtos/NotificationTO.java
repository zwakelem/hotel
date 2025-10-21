package za.co.simplitate.hotelbooking.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import za.co.simplitate.hotelbooking.util.enums.NotificationType;

import java.time.LocalDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationTO(
        Long id,
        String subject,
        String body,
        String recipient,
        NotificationType notificationType,
        String bookingReference,
        LocalDateTime createdAt
) {
}
