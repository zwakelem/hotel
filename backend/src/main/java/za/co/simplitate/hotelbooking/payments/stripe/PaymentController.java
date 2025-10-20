package za.co.simplitate.hotelbooking.payments.stripe;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.simplitate.hotelbooking.dtos.Response;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<Response> initiliasePayment(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(paymentRequest));
    }

    @PutMapping("/update")
    public void updatePaymentBooking(@RequestBody PaymentRequest paymentRequest) {
        paymentService.updatePaymentBooking(paymentRequest);
    }
}
