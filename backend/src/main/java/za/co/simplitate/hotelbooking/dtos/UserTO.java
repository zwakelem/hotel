package za.co.simplitate.hotelbooking.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        @JsonIgnore
        String password,
        String phoneNumber,
        String role,
        boolean isActive,
        LocalDate createdAt
) {
}
