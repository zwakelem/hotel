package za.co.simplitate.hotelbooking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.dtos.RoomTO;
import za.co.simplitate.hotelbooking.services.RoomService;
import za.co.simplitate.hotelbooking.util.enums.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> addRoom(
            @RequestParam Integer roomNumber,
            @RequestParam RoomType roomType,
            @RequestParam BigDecimal pricePerNight,
            @RequestParam Integer capacity,
            @RequestParam String description,
            @RequestParam MultipartFile imageFile) {

        RoomTO roomTO = RoomTO.builder()
                .roomNumber(roomNumber)
                .capacity(capacity)
                .roomType(roomType)
                .description(description)
                .pricePerNight(pricePerNight)
                .build();
        return ResponseEntity.ok(roomService.addRoom(roomTO, imageFile));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateRoom(
            @RequestParam (value = "roomNumber", required = false) Integer roomNumber,
            @RequestParam (value = "roomType", required = false) RoomType roomType,
            @RequestParam (value = "pricePerNight", required = false) BigDecimal pricePerNight,
            @RequestParam (value = "capacity", required = false) Integer capacity,
            @RequestParam (value = "description", required = false) String description,
            @RequestParam (value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam (value = "id") Long id
            ) {

        RoomTO roomTO = RoomTO.builder()
                .id(id)
                .roomNumber(roomNumber)
                .capacity(capacity)
                .roomType(roomType)
                .description(description)
                .pricePerNight(pricePerNight)
                .build();
        return ResponseEntity.ok(roomService.updateRoom(roomTO, imageFile));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.deleteRoom(id));
    }

    @GetMapping("/available")
    public ResponseEntity<Response> getAvailableRooms(
            @RequestParam LocalDate checkInDate,
            @RequestParam LocalDate checkOutDate,
            @RequestParam (required = false) RoomType roomType) {
        return ResponseEntity.ok(roomService.getAvailableRooms(checkInDate, checkOutDate, roomType));
    }

    @GetMapping("/types")
    public ResponseEntity<List<RoomType>> getRoomTypes() {
        return ResponseEntity.ok(roomService.getAllRoomTypes());
    }

    @GetMapping("/search")
    public ResponseEntity<Response> searchRoom(@RequestParam String input) {
        return ResponseEntity.ok(roomService.searchRoom(input));
    }

    @GetMapping("/roombytype")
    public ResponseEntity<Response> searchRoom(@RequestParam RoomType roomType) {
        return ResponseEntity.ok(roomService.getRoomsByType(roomType));
    }

}
