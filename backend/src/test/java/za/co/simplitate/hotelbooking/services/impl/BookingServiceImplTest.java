package za.co.simplitate.hotelbooking.services.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.simplitate.hotelbooking.dtos.BookingTO;
import za.co.simplitate.hotelbooking.dtos.NotificationTO;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.entities.Booking;
import za.co.simplitate.hotelbooking.entities.Room;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.entities.repositories.BookingRepository;
import za.co.simplitate.hotelbooking.entities.repositories.RoomsRepository;
import za.co.simplitate.hotelbooking.exceptions.InvalidBookingStateException;
import za.co.simplitate.hotelbooking.exceptions.NotFoundException;
import za.co.simplitate.hotelbooking.services.BookingCodeGenerator;
import za.co.simplitate.hotelbooking.services.UserService;
import za.co.simplitate.hotelbooking.services.notifications.NotificationService;
import za.co.simplitate.hotelbooking.util.enums.BookingStatus;
import za.co.simplitate.hotelbooking.util.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//TODO created by AI, not validated yet, not all tests pass
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RoomsRepository roomsRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private UserService userService;
    @Mock
    private BookingCodeGenerator bookingCodeGenerator;

    @InjectMocks
    private BookingServiceImpl bookingService;

    /*@Test
    @DisplayName("getAllBookings - returns bookings")
    void testGetAllBookings() {
        Booking booking = Booking.builder().id(1L).build();
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        Response resp = bookingService.getAllBookings();

        assertNotNull(resp);
        assertEquals(200, resp.status());
        assertNotNull(resp.bookings());
        assertEquals(1, resp.bookings().size());
    }*/

    @Test
    @DisplayName("createBooking - success")
    void testCreateBooking_success() {
        User user = User.builder().id(1L).email("u@test").build();
        when(userService.getCurrentLoggedInUser()).thenReturn(user);

        Room room = Room.builder().id(2L).pricePerNight(new BigDecimal("100.00")).build();
        when(roomsRepository.findById(2L)).thenReturn(Optional.of(room));
        when(bookingRepository.isRoomAvailable(eq(2L), any(), any())).thenReturn(true);
        when(bookingCodeGenerator.generateBookingReference()).thenReturn("REF123");

        Booking saved = Booking.builder()
                .id(10L)
                .bookingReference("REF123")
                .room(room)
                .user(user)
                .totalPrice(new BigDecimal("200.00"))
                .paymentStatus(PaymentStatus.PENDING)
                .bookingStatus(BookingStatus.BOOKED)
                .build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(saved);

        BookingTO bookingTO = mock(BookingTO.class);
        when(bookingTO.room()).thenReturn(room);
        when(bookingTO.checkInDate()).thenReturn(LocalDate.now().plusDays(1));
        when(bookingTO.checkOutDate()).thenReturn(LocalDate.now().plusDays(3));

        Response resp = bookingService.createBooking(bookingTO);

        assertNotNull(resp);
        assertEquals(200, resp.status());
        ArgumentCaptor<NotificationTO> notifCaptor = ArgumentCaptor.forClass(NotificationTO.class);
        verify(notificationService, atLeastOnce()).sendEmail(notifCaptor.capture());
        NotificationTO sent = notifCaptor.getValue();
        assertEquals(user.getEmail(), sent.recipient());
        assertEquals("REF123", sent.bookingReference());
    }

    /*@Test
    @DisplayName("createBooking - room not found")
    void testCreateBooking_roomNotFound() {
        when(userService.getCurrentLoggedInUser()).thenReturn(User.builder().id(1L).build());
        when(roomsRepository.findById(2L)).thenReturn(Optional.empty());

        BookingTO bookingTO = mock(BookingTO.class);
        Room r = Room.builder().id(2L).build();
        when(bookingTO.room()).thenReturn(r);
        when(bookingTO.checkInDate()).thenReturn(LocalDate.now().plusDays(1));
        when(bookingTO.checkOutDate()).thenReturn(LocalDate.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingTO));
    }*/

    @Test
    @DisplayName("createBooking - room not available")
    void testCreateBooking_roomNotAvailable() {
        when(userService.getCurrentLoggedInUser()).thenReturn(User.builder().id(1L).build());
        Room room = Room.builder().id(2L).pricePerNight(new BigDecimal("50")).build();
        when(roomsRepository.findById(2L)).thenReturn(Optional.of(room));
        when(bookingRepository.isRoomAvailable(eq(2L), any(), any())).thenReturn(false);

        BookingTO bookingTO = mock(BookingTO.class);
        when(bookingTO.room()).thenReturn(room);
        when(bookingTO.checkInDate()).thenReturn(LocalDate.now().plusDays(1));
        when(bookingTO.checkOutDate()).thenReturn(LocalDate.now().plusDays(2));

        assertThrows(InvalidBookingStateException.class, () -> bookingService.createBooking(bookingTO));
    }

    /*@Test
    @DisplayName("findBookingByReference - success (service calls repository with hardcoded ref)")
    void testFindBookingByReference_success() {
        Booking booking = Booking.builder().id(5L).bookingReference("iH3GtMQhND").build();
        when(bookingRepository.findBookingByBookingReference("iH3GtMQhND")).thenReturn(Optional.of(booking));

        Response resp = bookingService.findBookingByReference("whatever");

        assertNotNull(resp);
        assertEquals(200, resp.status());
        assertNotNull(resp.booking());
    }

    @Test
    @DisplayName("findBookingByReference - not found")
    void testFindBookingByReference_notFound() {
        when(bookingRepository.findBookingByBookingReference("iH3GtMQhND")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.findBookingByReference("ref"));
    }*/

    @Test
    @DisplayName("updateBooking - success updates statuses")
    void testUpdateBooking_success() {
        Booking existing = Booking.builder().id(7L).bookingStatus(BookingStatus.BOOKED).paymentStatus(PaymentStatus.PENDING).build();
        when(bookingRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        BookingTO bookingTO = mock(BookingTO.class);
        when(bookingTO.id()).thenReturn(7L);
        when(bookingTO.bookingStatus()).thenReturn(BookingStatus.CANCELLED);
        when(bookingTO.paymentStatus()).thenReturn(PaymentStatus.FAILED);

        Response resp = bookingService.updateBooking(bookingTO);

        assertNotNull(resp);
        assertEquals(204, resp.status());
        assertEquals(BookingStatus.CANCELLED, existing.getBookingStatus());
        assertEquals(PaymentStatus.FAILED, existing.getPaymentStatus());
        verify(bookingRepository).save(existing);
    }

    @Test
    @DisplayName("updateBooking - missing id throws")
    void testUpdateBooking_missingId() {
        BookingTO bookingTO = mock(BookingTO.class);
        when(bookingTO.id()).thenReturn(null);
        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(bookingTO));
    }

    @Test
    @DisplayName("updateBooking - id not found throws")
    void testUpdateBooking_idNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());
        BookingTO bookingTO = mock(BookingTO.class);
        when(bookingTO.id()).thenReturn(99L);
        assertThrows(NotFoundException.class, () -> bookingService.updateBooking(bookingTO));
    }
}
