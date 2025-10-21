package za.co.simplitate.hotelbooking.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.simplitate.hotelbooking.entities.BookingReference;
import za.co.simplitate.hotelbooking.entities.repositories.BookingReferenceRepository;

import static java.lang.Math.random;

@Service
@RequiredArgsConstructor
public class BookingCodeGenerator {

    private final BookingReferenceRepository bookingReferenceRepository;

    public String generateBookingReference() {
        String bookingRef;
        do {
            bookingRef = generateRandonAlphaNumericCode(10);
        } while(isBookingRefExists(bookingRef));

        persistBookingReference(bookingRef);
        return bookingRef;
    }

    private boolean isBookingRefExists(String bookingRef) {
        return bookingReferenceRepository.findBookingReferenceByReferenceNumber(bookingRef).isPresent();
    }

    private void persistBookingReference(String ref) {
        BookingReference bookingRefEntity = BookingReference.builder()
                .referenceNumber(ref)
                .build();
        bookingReferenceRepository.save(bookingRefEntity);
    }

    private String generateRandonAlphaNumericCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (random() * characters.length());
            result.append(characters.charAt(randomIndex));
        }
        return result.toString();
    }
}
