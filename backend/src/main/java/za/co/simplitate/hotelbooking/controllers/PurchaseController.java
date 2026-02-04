package za.co.simplitate.hotelbooking.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.PurchaseTO;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.services.PurchaseService;
import za.co.simplitate.hotelbooking.services.security.AuthUser;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public Mono<Response> createPurchase(@RequestBody PurchaseTO purchaseTO, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.createPurchase(purchaseTO, userId)
                .map(purchase -> Response.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Purchase created successfully")
                        .purchase(purchase)
                        .build());
    }

    @PutMapping("/{id}")
    public Mono<Response> updatePurchase(@PathVariable Long id, @RequestBody PurchaseTO purchaseTO, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.updatePurchase(id, purchaseTO, userId)
                .map(purchase -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchase updated successfully")
                        .purchase(purchase)
                        .build());
    }

    @DeleteMapping("/{id}")
    public Mono<Response> deletePurchase(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.deletePurchase(id, userId)
                .then(Mono.just(Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchase deleted successfully")
                        .build()));
    }

    @GetMapping("/{id}")
    public Mono<Response> getPurchaseById(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.getPurchaseById(id, userId)
                .map(purchase -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchase retrieved successfully")
                        .purchase(purchase)
                        .build());
    }

    @GetMapping
    public Mono<Response> getAllPurchases(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.getAllPurchasesByUser(userId)
                .collectList()
                .map(purchases -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchases retrieved successfully")
                        .purchaseList(purchases)
                        .build());
    }

    @GetMapping("/category/{categoryId}")
    public Mono<Response> getPurchasesByCategory(@PathVariable Long categoryId, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.getPurchasesByCategory(categoryId, userId)
                .collectList()
                .map(purchases -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchases retrieved successfully")
                        .purchaseList(purchases)
                        .build());
    }

    @GetMapping("/date-range")
    public Mono<Response> getPurchasesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.getPurchasesByDateRange(startDate, endDate, userId)
                .collectList()
                .map(purchases -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchases retrieved successfully")
                        .purchaseList(purchases)
                        .build());
    }

    @GetMapping("/summary/day")
    public Mono<Response> getPurchaseSummaryByDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.getPurchaseSummaryByDay(date, userId)
                .map(summary -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchase summary retrieved successfully")
                        .purchaseSummary(summary)
                        .build());
    }

    @GetMapping("/summary/week")
    public Mono<Response> getPurchaseSummaryByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.getPurchaseSummaryByWeek(date, userId)
                .map(summary -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchase summary retrieved successfully")
                        .purchaseSummary(summary)
                        .build());
    }

    @GetMapping("/summary/month")
    public Mono<Response> getPurchaseSummaryByMonth(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.getPurchaseSummaryByMonth(year, month, userId)
                .map(summary -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchase summary retrieved successfully")
                        .purchaseSummary(summary)
                        .build());
    }

    @GetMapping("/summary/year")
    public Mono<Response> getPurchaseSummaryByYear(
            @RequestParam int year,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return purchaseService.getPurchaseSummaryByYear(year, userId)
                .map(summary -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Purchase summary retrieved successfully")
                        .purchaseSummary(summary)
                        .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return authUser.getUser().getId();
    }
}
