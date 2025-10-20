package za.co.simplitate.hotelbooking;

import za.co.simplitate.hotelbooking.dtos.LoginRequest;
import za.co.simplitate.hotelbooking.dtos.RegistrationRequest;
import za.co.simplitate.hotelbooking.entities.User;

import static za.co.simplitate.hotelbooking.TestConstants.*;

public class MockData {

    public static RegistrationRequest mockRegistrationRequest() {
        return new RegistrationRequest(FIRST_NAME, LAST_NAME, EMAIL, PHONE_NUMBER, ROLE, PASSWORD);
    }

    public static LoginRequest mockLoginRequest() {
        return new LoginRequest(EMAIL, PASSWORD);
    }

    public static User mockUser() {
        return User.builder()
                .id(1L)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .password(PASSWORD)
                .phoneNumber(PHONE_NUMBER)
                .role(ROLE)
                .isActive(Boolean.TRUE).build();
    }
}
