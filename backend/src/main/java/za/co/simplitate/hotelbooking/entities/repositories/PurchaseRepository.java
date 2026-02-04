package za.co.simplitate.hotelbooking.entities.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.entities.Purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface PurchaseRepository extends ReactiveCrudRepository<Purchase, Long> {

    Flux<Purchase> findByUserId(Long userId);

    Mono<Purchase> findByIdAndUserId(Long id, Long userId);

    Flux<Purchase> findByUserIdAndCategoryId(Long userId, Long categoryId);

    Flux<Purchase> findByUserIdAndPurchaseDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(total), 0) FROM purchase WHERE user_id = :userId AND purchase_date BETWEEN :startDate AND :endDate")
    Mono<BigDecimal> sumTotalByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(total), 0) FROM purchase WHERE user_id = :userId AND category_id = :categoryId AND purchase_date BETWEEN :startDate AND :endDate")
    Mono<BigDecimal> sumTotalByUserIdAndCategoryIdAndDateBetween(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);
}
