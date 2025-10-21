package za.co.simplitate.hotelbooking.services.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.entities.repositories.BookingRepository;
import za.co.simplitate.hotelbooking.entities.repositories.UserRepository;
import za.co.simplitate.hotelbooking.services.security.JWTUtils;
import za.co.simplitate.hotelbooking.util.exceptions.InvalidCredentialsException;
import za.co.simplitate.hotelbooking.util.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static za.co.simplitate.hotelbooking.MockData.*;
import static za.co.simplitate.hotelbooking.TestConstants.EMAIL;
import static za.co.simplitate.hotelbooking.TestConstants.TOKEN;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JWTUtils jwtUtils;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("registerUser - greenline")
    void testRegisterUser() {
        when(userRepository.save(any(User.class))).thenReturn(new User());

        var result = userService.registerUser(mockRegistrationRequest());

        assertNotNull(result);
        assertEquals(200, result.status());
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).save(userArgumentCaptor.capture());
        assertTrue(userArgumentCaptor.getValue().isActive());
    }

    @Test
    @DisplayName("loginUser - greenline")
    void testLoginUser() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockUser()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtils.generateToken(any())).thenReturn(TOKEN);

        var result = userService.loginUser(mockLoginRequest());

        assertNotNull(result);
        assertEquals(200, result.status());
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, atLeastOnce()).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("loginUser - user not found by email")
    void testLoginUser_notFoundByEmail() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userService.loginUser(mockLoginRequest()));
        assertTrue(thrown.getMessage().startsWith("User not found"));
    }

    @Test
    @DisplayName("loginUser - passwords do not match")
    void testLoginUser_passwordsNotMatching() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockUser()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        InvalidCredentialsException thrown = assertThrows(InvalidCredentialsException.class,
                                                    () -> userService.loginUser(mockLoginRequest()));
        assertEquals("Password does not match!!", thrown.getMessage());
    }

    @Test
    @DisplayName("getAllUsers - greenline")
    void testGetAllUsers() {
        when(userRepository.findAll(any(Sort.class))).thenReturn(List.of(mockUser()));

        var result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(200, result.status());
        assertEquals(1, result.users().size());
    }

    @Test
    @DisplayName("getAllUsers - users not found")
    void testGetAllUsers_notFound() {
        when(userRepository.findAll(any(Sort.class))).thenReturn(new ArrayList<>());

        NotFoundException thrown = assertThrows(NotFoundException.class,
                () -> userService.getAllUsers());
        assertEquals("No users found!!", thrown.getMessage());
    }

    /*@Test
    void getOwnAccountDetails() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockUser()));

        var result = userService.getOwnAccountDetails();
        assertNotNull(result);
    }*/

    @Test
    void getCurrentLoggedInUser() {
    }

    @Test
    void updateOwnAccount() {
    }

    @Test
    void getBookingHistory() {
    }

    @Test
    void deleteOwnAccount() {
    }
}
