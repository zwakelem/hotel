package za.co.simplitate.hotelbooking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.simplitate.hotelbooking.dtos.BookingTO;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.services.BookingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CUSTOMER')")
    public ResponseEntity<Response> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PostMapping()
    public ResponseEntity<Response> createBooking(@RequestBody BookingTO bookingTO) {
        return ResponseEntity.ok(bookingService.createBooking(bookingTO));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<Response> getBookingByReference(@PathVariable String reference) {
        return ResponseEntity.ok(bookingService.findBookingByReference(reference));
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateBooking(@RequestBody BookingTO bookingTO) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingTO));
    }
}
