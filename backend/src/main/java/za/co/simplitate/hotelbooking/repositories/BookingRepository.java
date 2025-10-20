package za.co.simplitate.hotelbooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.simplitate.hotelbooking.entities.Booking;
import za.co.simplitate.hotelbooking.entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingsByUser(User user);

    Optional<Booking> findBookingByBookingReference(String bookingReference);

    boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);


}
