package za.co.simplitate.hotelbooking.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import za.co.simplitate.hotelbooking.util.enums.UserRole;

@Builder
public record RegistrationRequest(
        @NotBlank(message = "FirstName is required")
        String firstName,
        @NotBlank(message = "LastName is required")
        String lastName,
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "PhoneNumber is required")
        String phoneNumber,
        UserRole role,
        @NotBlank(message = "Password is required")
        String password
) { }
