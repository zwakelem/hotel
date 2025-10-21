package za.co.simplitate.hotelbooking.services;

import org.springframework.web.multipart.MultipartFile;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.dtos.RoomTO;
import za.co.simplitate.hotelbooking.util.enums.RoomType;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    Response addRoom(RoomTO roomTO, MultipartFile imageFile);
    Response updateRoom(RoomTO roomTO, MultipartFile imageFile);
    Response getAllRooms();
    Response getRoomById(Long roomId);
    Response deleteRoom(Long roomId);
    Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType);
    List<RoomType> getAllRoomTypes();
    Response searchRoom(String input);
    Response getRoomsByType(RoomType roomType);
}
