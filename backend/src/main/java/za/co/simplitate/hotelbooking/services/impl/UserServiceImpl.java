package za.co.simplitate.hotelbooking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.co.simplitate.hotelbooking.dtos.*;
import za.co.simplitate.hotelbooking.entities.Booking;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.entities.repositories.BookingRepository;
import za.co.simplitate.hotelbooking.entities.repositories.UserRepository;
import za.co.simplitate.hotelbooking.exceptions.InvalidCredentialsException;
import za.co.simplitate.hotelbooking.exceptions.NotFoundException;
import za.co.simplitate.hotelbooking.services.UserService;
import za.co.simplitate.hotelbooking.services.security.JWTUtils;
import za.co.simplitate.hotelbooking.util.GenericMapper;
import za.co.simplitate.hotelbooking.util.enums.UserRole;

import java.util.ArrayList;
import java.util.List;

import static za.co.simplitate.hotelbooking.Const.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String PASSWORD_DOES_NOT_MATCH = "Password does not match!!";
    public static final String USER_EMAIL_NOT_FOUND = "User not found by email=%s!!";
    public static final String LOGGED_IN_SUCCESSFULLY = "user logged in successfully";
    public static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully!!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwtUtils;
    private final BookingRepository bookingRepository;


    @Override
    public Response registerUser(RegistrationRequest registrationRequest) {
        log.info("registerUser: ");
        UserRole userRole = registrationRequest.role() != null ? registrationRequest.role() : UserRole.CUSTOMER;
        User userToSave = createUser(registrationRequest, userRole);
        userRepository.save(userToSave);
        return Response.builder()
                .status(200)
                .message(USER_REGISTERED_SUCCESSFULLY)
                .build();
    }

    private User createUser(RegistrationRequest registrationRequest, UserRole userRole) {
        return User.builder()
                .firstName(registrationRequest.firstName())
                .lastName(registrationRequest.lastName())
                .email(registrationRequest.email())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .phoneNumber(registrationRequest.phoneNumber())
                .isActive(Boolean.TRUE)
                .role(userRole)
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {
        log.info("loginUser: ");
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> {
                    var message = String.format(USER_EMAIL_NOT_FOUND, loginRequest.email());
                    log.warn(message);
                    return new NotFoundException(message);
                });

        if(!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException(PASSWORD_DOES_NOT_MATCH);
        }

        String token = jwtUtils.generateToken(user.getEmail());
        return Response.builder()
                .status(200)
                .message(LOGGED_IN_SUCCESSFULLY)
                .role(user.getRole())
                .token(token)
                .active(user.isActive())
                .expirationTime("6 months")
                .build();
    }

    @Override
    public Response getAllUsers() {
        log.info("getAllUsers: ");
        List<UserTO> userTOList;
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        if (!users.isEmpty()) {
            userTOList = users.parallelStream()
                    .map(GenericMapper::mapToUserTO)
                    .toList();
        } else {
            throw new NotFoundException("No users found!!");
        }
        return Response.builder()
                .status(200)
                .users(userTOList)
                .message(SUCCESS)
                .build();
    }

    @Override
    public Response getOwnAccountDetails() {
        log.info("getOwnAccountDetails: ");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    var message = String.format(USER_EMAIL_NOT_FOUND, email);
                    log.warn(message);
                    return new NotFoundException(message);
                });
        UserTO userTO = GenericMapper.mapToUserTO(user);
        return Response.builder()
                .status(200)
                .message(SUCCESS)
                .user(userTO)
                .build();
    }

    @Override
    public User getCurrentLoggedInUser() {
        log.info("getCurrentLoggedInUser: ");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    var message = String.format(USER_EMAIL_NOT_FOUND, email);
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }

    @Override
    public Response updateOwnAccount(UserTO userTO) {
        log.info("updateOwnAccount: ");
        User user = getCurrentLoggedInUser();

        if(userTO.email() != null)
            user.setEmail(userTO.email());
        if(userTO.firstName() != null)
            user.setFirstName(userTO.firstName());
        if(userTO.lastName() != null)
            user.setLastName(userTO.lastName());
        if(userTO.phoneNumber() != null)
            user.setPhoneNumber(userTO.phoneNumber());
        if(userTO.password() != null && !userTO.password().isBlank())
            user.setPassword(passwordEncoder.encode(userTO.password()));

        userRepository.save(user);
        return Response.builder()
                .status(200)
                .message("User updated successfully!!")
                .user(userTO)
                .build();
    }

    @Override
    public Response getBookingHistory() {
        log.info("getBookingHistory: ");
        User user = getCurrentLoggedInUser();
        List<Booking> bookingList = bookingRepository.findBookingsByUser(user);
        List<BookingTO> bookingTOList = new ArrayList<>();
        if(!bookingList.isEmpty()) {
            bookingTOList = bookingList.stream().map(GenericMapper::mapToBookingTO).toList();
        }
        return Response.builder()
                .status(200)
                .bookings(bookingTOList)
                .message(SUCCESS)
                .build();
    }

    @Override
    public Response deleteOwnAccount() {
        log.info("deleteOwnAccount: ");
        User user = getCurrentLoggedInUser();
        userRepository.delete(user);
        return Response.builder()
                .status(200)
                .message("User deleted successfully!!")
                .build();
    }
}
