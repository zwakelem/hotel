package za.co.simplitate.hotelbooking.services.payments.stripe;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.simplitate.hotelbooking.dtos.NotificationTO;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.entities.Booking;
import za.co.simplitate.hotelbooking.entities.PaymentEntity;
import za.co.simplitate.hotelbooking.entities.repositories.BookingRepository;
import za.co.simplitate.hotelbooking.entities.repositories.PaymentRepository;
import za.co.simplitate.hotelbooking.services.notifications.NotificationService;
import za.co.simplitate.hotelbooking.util.enums.NotificationType;
import za.co.simplitate.hotelbooking.util.enums.PaymentGateway;
import za.co.simplitate.hotelbooking.util.enums.PaymentStatus;
import za.co.simplitate.hotelbooking.util.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static za.co.simplitate.hotelbooking.Const.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public Response createPaymentIntent(PaymentRequest paymentRequest) {
        log.info("createPaymentIntent: ");

        String bookingReference = getAndValidateBookingRef(paymentRequest);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // convert to cents
                    .setCurrency("usd")
                    .putMetadata("bookingReference", bookingReference)
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            String uniqueTransactionId = intent.getClientSecret();

            return Response.builder()
                    .status(200)
                    .message(SUCCESS)
                    .transactionId(uniqueTransactionId)
                    .build();

        } catch (Exception ex) {
            throw new RuntimeException("Error creating transaction id");
        }
    }

    public void updatePaymentBooking(PaymentRequest paymentRequest) {
        log.info("updatePaymentBooking: ...");
        String bookingRef = paymentRequest.getBookingReference();
        Booking booking = bookingRepository.findBookingByBookingReference(bookingRef)
                .orElseThrow(() -> new NotFoundException("Booking not found!!"));

        PaymentEntity payment = createPaymentEntity(paymentRequest, bookingRef, booking);

        if(!paymentRequest.isSuccess()) {
            payment.setFailureReason(paymentRequest.getFailureReason());
        }

        paymentRepository.save(payment);
        createAndSendNotification(paymentRequest, booking, bookingRef);
    }

    private void createAndSendNotification(PaymentRequest paymentRequest, Booking booking, String bookingRef) {
        NotificationTO notificationTO = NotificationTO.builder()
                .recipient(booking.getUser().getEmail())
                .notificationType(NotificationType.EMAIL)
                .bookingReference(bookingRef)
                .subject(getNotificationSubject(paymentRequest.isSuccess()))
                .body(getNotificationBody(paymentRequest.isSuccess(), bookingRef))
                .build();

        booking.setPaymentStatus(paymentRequest.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        bookingRepository.save(booking); // update booking status

        notificationService.sendEmail(notificationTO);
    }

    private String getNotificationSubject(boolean isSuccess) {
        return isSuccess ? "Booking Payment Successful" : "Booking Payment failed";
    }

    private String getNotificationBody(boolean isSuccess, String bookingRef) {
        return isSuccess
            ? String.format("Thank you for your business. Your payment for booking with reference: %s is successful", bookingRef)
            : String.format("Payment for booking reference: %s failed!!", bookingRef);
    }

    private static PaymentEntity createPaymentEntity(PaymentRequest paymentRequest, String bookingRef, Booking booking) {
        return PaymentEntity.builder()
                .paymentGateway(PaymentGateway.STRIPE)
                .amount(paymentRequest.getAmount())
                .transactionId(paymentRequest.getTransactionId())
                .paymentStatus(paymentRequest.isSuccess() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                .paymentDate(LocalDateTime.now())
                .bookingReference(bookingRef)
                .user(booking.getUser())
                .build();
    }

    private String getAndValidateBookingRef(PaymentRequest paymentRequest) {
        String bookingReference = paymentRequest.getBookingReference();
        Booking booking = bookingRepository.findBookingByBookingReference(bookingReference)
                .orElseThrow(() -> new NotFoundException("Booking not found!!"));

        if(booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new NotFoundException("Payment already made!!");
        }

        if(booking.getTotalPrice().compareTo(paymentRequest.getAmount()) != 0) {
            throw new NotFoundException("Payment amount does not tally!!");
        }
        return bookingReference;
    }
}
