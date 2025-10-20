package za.co.simplitate.hotelbooking.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.simplitate.hotelbooking.dtos.LoginRequest;
import za.co.simplitate.hotelbooking.dtos.RegistrationRequest;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {
        log.info("registerUser:: {}", registrationRequest.email());
        return ResponseEntity.ok(userService.registerUser(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("loginUser:: {}", loginRequest.email());
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }
}
