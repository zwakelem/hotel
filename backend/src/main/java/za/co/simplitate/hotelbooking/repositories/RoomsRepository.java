package za.co.simplitate.hotelbooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.simplitate.hotelbooking.entities.Room;
import za.co.simplitate.hotelbooking.enums.RoomType;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomsRepository extends JpaRepository<Room, Long> {

    //TODO not sure what to do with the dates
    List<Room> findAvailableRooms(RoomType roomType);
//    List<Room> findRoomByRoomType (LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType);

    List<Room> findByDescription(String searchParam);

    List<Room> findRoomByRoomType(RoomType roomType);
}
