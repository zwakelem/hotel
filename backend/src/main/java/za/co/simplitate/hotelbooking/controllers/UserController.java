package za.co.simplitate.hotelbooking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.dtos.UserTO;
import za.co.simplitate.hotelbooking.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')") // only admin user can call this endpoint
    public ResponseEntity<Response> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/update")
    public ResponseEntity<Response> updateOwnAccount(@RequestBody UserTO userTO) {
        return ResponseEntity.ok(userService.updateOwnAccount(userTO));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Response> deleteOwnAccount() {
        return ResponseEntity.ok(userService.deleteOwnAccount());
    }

    @GetMapping("/account")
    public ResponseEntity<Response> getOwnAccountDetails() {
        return ResponseEntity.ok(userService.getOwnAccountDetails());
    }

    @GetMapping("/bookings")
    public ResponseEntity<Response> getBookingHistory() {
        return ResponseEntity.ok(userService.getBookingHistory());
    }

}
