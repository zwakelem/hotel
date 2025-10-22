package za.co.simplitate.hotelbooking.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import za.co.simplitate.hotelbooking.dtos.BookingRequestTO;
import za.co.simplitate.hotelbooking.dtos.BookingTO;
import za.co.simplitate.hotelbooking.dtos.NotificationTO;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.entities.Booking;
import za.co.simplitate.hotelbooking.entities.Room;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.entities.repositories.BookingRepository;
import za.co.simplitate.hotelbooking.entities.repositories.RoomsRepository;
import za.co.simplitate.hotelbooking.services.BookingCodeGenerator;
import za.co.simplitate.hotelbooking.services.BookingService;
import za.co.simplitate.hotelbooking.services.UserService;
import za.co.simplitate.hotelbooking.services.notifications.NotificationService;
import za.co.simplitate.hotelbooking.util.GenericMapper;
import za.co.simplitate.hotelbooking.util.enums.BookingStatus;
import za.co.simplitate.hotelbooking.util.enums.PaymentStatus;
import za.co.simplitate.hotelbooking.util.exceptions.InvalidBookingStateException;
import za.co.simplitate.hotelbooking.util.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static za.co.simplitate.hotelbooking.Const.ROOM_NOT_FOUND;
import static za.co.simplitate.hotelbooking.Const.SUCCESS;
import static za.co.simplitate.hotelbooking.util.CommonUtil.validateDates;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final String BOOKING_CREATED_SUCCESSFULLY = "booking created successfully";
    private static final String BOOKING_CONFIRMATION = "BOOKING CONFIRMATION";
    private static final String BOOKING_REF_NOT_FOUND = "Booking with ref=%s not found!!";
    private static final String BOOKING_ID_NOT_FOUND = "Booking with ref=%d not found!!";

    private final BookingRepository bookingRepository;

    private final RoomsRepository roomsRepository;

    private final NotificationService notificationService;

    private final UserService userService;

    private final BookingCodeGenerator bookingCodeGenerator;

    @Override
    public Response getAllBookings() {
        log.info("getAllBookings: ");
        List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<BookingTO> bookingTOList = bookingList.stream()
                .map(GenericMapper::mapMinimalBookingTO)
                .toList();
        return Response.builder()
                .message(SUCCESS)
                .bookings(bookingTOList)
                .status(200)
                .build();
    }

    @Override
    public Response createBooking(BookingRequestTO bookingRequestTO) {
        log.info("createBooking: ");
        validateDates(bookingRequestTO.checkInDate(), bookingRequestTO.checkOutDate());
        checkRoomAvailability(bookingRequestTO);
        User currentUser = userService.getCurrentLoggedInUser();

        Room room = roomsRepository.findById(bookingRequestTO.roomId())
                .orElseThrow(() -> {
                    var message = String.format(ROOM_NOT_FOUND, bookingRequestTO.roomId());
                    log.warn(message);
                    return new NotFoundException(message);
                });

        BigDecimal totalPrice = calculateTotalPrice(room, bookingRequestTO);
        String bookingRef = bookingCodeGenerator.generateBookingReference();
        Booking booking = createBooking(bookingRequestTO, currentUser, room, totalPrice, bookingRef);
        Booking persistedBooking = bookingRepository.save(booking);
        BookingTO bookingTO = GenericMapper.mapToBookingTO(persistedBooking);

        String paymentLink = "http://localhost:4200/payment" + bookingRef + "/" + totalPrice;
        log.info("Booking payment link {}", paymentLink);

        String emailMessage = String.format(""" 
                        Your booking has been successfully created.
                        Please process with the payment using the link below
                        %s
                        """, paymentLink);
        NotificationTO notificationTO = createNotification(currentUser, emailMessage, bookingRef);
        notificationService.sendEmail(notificationTO);

        return Response.builder()
                .status(200)
                .message(BOOKING_CREATED_SUCCESSFULLY)
                .booking(bookingTO)
                .build();
    }

    private static NotificationTO createNotification(User currentUser, String emailMessage, String bookingRef) {
        return NotificationTO.builder()
                .recipient(currentUser.getEmail())
                .subject(BOOKING_CONFIRMATION)
                .body(emailMessage)
                .bookingReference(bookingRef)
                .build();
    }

    private static Booking createBooking(BookingRequestTO bookingRequestTO, User currentUser, Room room,
                                         BigDecimal totalPrice, String bookingRef) {
        return Booking.builder()
                .user(currentUser)
                .room(room)
                .checkInDate(bookingRequestTO.checkInDate())
                .checkOutDate(bookingRequestTO.checkOutDate())
                .totalPrice(totalPrice)
                .bookingReference(bookingRef)
                .paymentStatus(PaymentStatus.PENDING)
                .bookingStatus(BookingStatus.BOOKED)
                .createdAt(LocalDate.now())
                .build();
    }

    private void checkRoomAvailability(BookingRequestTO bookingRequestTO) {
        boolean isAvaiable = bookingRepository.isRoomAvailable(bookingRequestTO.roomId(),
                                                                bookingRequestTO.checkInDate(),
                                                                bookingRequestTO.checkOutDate());
        if(!isAvaiable) {
            throw new InvalidBookingStateException("Room is not available to be booked");
        }
    }

    private BigDecimal calculateTotalPrice(Room room, BookingRequestTO bookingRequestTO) {
        BigDecimal pricePerNight = room.getPricePerNight();
        long days = ChronoUnit.DAYS.between(bookingRequestTO.checkInDate(), bookingRequestTO.checkOutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }

    @Override
    public Response findBookingByReference(String ref) {
        log.info("findBookingByReference: ref={}", ref);
        //Booking booking = bookingRepository.findBookingByBookingReference("iH3GtMQhND")
        Booking booking = bookingRepository.findBookingByBookingReference(ref)
                .orElseThrow(() -> {
                    var message = String.format(BOOKING_REF_NOT_FOUND, ref);
                    log.warn(message);
                    return new NotFoundException(message);
                });
        BookingTO bookingTO = GenericMapper.mapToBookingTO(booking);
        return Response.builder()
                .status(200)
                .message(SUCCESS)
                .booking(bookingTO)
                .build();
    }

    @Override
    public Response updateBooking(BookingTO bookingTO) {
        log.info("updateBooking: ");
        Booking existingBooking = queryBooking(bookingTO);

        if(bookingTO.bookingStatus() != null) {
            existingBooking.setBookingStatus(bookingTO.bookingStatus());
        }
        if(bookingTO.paymentStatus() != null) {
            existingBooking.setPaymentStatus(bookingTO.paymentStatus());
        }

        bookingRepository.save(existingBooking);
        return Response.builder()
                .status(204)
                .message("Booking updated successfully")
                .booking(bookingTO)
                .build();
    }

    private Booking queryBooking(BookingTO bookingTO) {
        if(bookingTO.id() == null)
            throw new NotFoundException("Booking Id is required");
        return bookingRepository.findById(bookingTO.id())
                .orElseThrow(() -> {
                    var message = String.format(BOOKING_ID_NOT_FOUND,  bookingTO.id());
                    log.warn(message);
                    return new NotFoundException(message);
                });
    }
}
