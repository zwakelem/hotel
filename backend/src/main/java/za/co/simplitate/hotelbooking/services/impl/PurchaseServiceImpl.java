package za.co.simplitate.hotelbooking.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.PurchaseSummaryTO;
import za.co.simplitate.hotelbooking.dtos.PurchaseTO;
import za.co.simplitate.hotelbooking.entities.Category;
import za.co.simplitate.hotelbooking.entities.Purchase;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.entities.repositories.CategoryRepository;
import za.co.simplitate.hotelbooking.entities.repositories.PurchaseRepository;
import za.co.simplitate.hotelbooking.services.PurchaseService;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Mono<PurchaseTO> createPurchase(PurchaseTO purchaseTO, Long userId) {
        Purchase purchase = Purchase.builder()
                .purchaseDate(purchaseTO.getPurchaseDate())
                .merchant(purchaseTO.getMerchant())
                .merchantAddress(purchaseTO.getMerchantAddress())
                .price(purchaseTO.getPrice())
                .vat(purchaseTO.getVat())
                .total(purchaseTO.getTotal())
                .quantity(purchaseTO.getQuantity())
                .link(purchaseTO.getLink())
                .user(User.builder().id(userId).build())
                .build();

        if (purchaseTO.getCategoryId() != null) {
            purchase.setCategory(Category.builder().id(purchaseTO.getCategoryId()).build());
        }

        return purchaseRepository.save(purchase)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Mono<PurchaseTO> updatePurchase(Long id, PurchaseTO purchaseTO, Long userId) {
        return purchaseRepository.findByIdAndUserId(id, userId)
                .flatMap(existingPurchase -> {
                    existingPurchase.setPurchaseDate(purchaseTO.getPurchaseDate());
                    existingPurchase.setMerchant(purchaseTO.getMerchant());
                    existingPurchase.setMerchantAddress(purchaseTO.getMerchantAddress());
                    existingPurchase.setPrice(purchaseTO.getPrice());
                    existingPurchase.setVat(purchaseTO.getVat());
                    existingPurchase.setTotal(purchaseTO.getTotal());
                    existingPurchase.setQuantity(purchaseTO.getQuantity());
                    existingPurchase.setLink(purchaseTO.getLink());

                    if (purchaseTO.getCategoryId() != null) {
                        existingPurchase.setCategory(Category.builder().id(purchaseTO.getCategoryId()).build());
                    } else {
                        existingPurchase.setCategory(null);
                    }

                    return purchaseRepository.save(existingPurchase);
                })
                .flatMap(this::mapToDTO);
    }

    @Override
    public Mono<Void> deletePurchase(Long id, Long userId) {
        return purchaseRepository.findByIdAndUserId(id, userId)
                .flatMap(purchaseRepository::delete);
    }

    @Override
    public Mono<PurchaseTO> getPurchaseById(Long id, Long userId) {
        return purchaseRepository.findByIdAndUserId(id, userId)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Flux<PurchaseTO> getAllPurchasesByUser(Long userId) {
        return purchaseRepository.findByUserId(userId)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Flux<PurchaseTO> getPurchasesByCategory(Long categoryId, Long userId) {
        return purchaseRepository.findByUserIdAndCategoryId(userId, categoryId)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Flux<PurchaseTO> getPurchasesByDateRange(LocalDate startDate, LocalDate endDate, Long userId) {
        return purchaseRepository.findByUserIdAndPurchaseDateBetween(userId, startDate, endDate)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Mono<PurchaseSummaryTO> getPurchaseSummaryByDay(LocalDate date, Long userId) {
        return purchaseRepository.findByUserIdAndPurchaseDateBetween(userId, date, date)
                .collectList()
                .flatMap(purchases -> {
                    BigDecimal total = purchases.stream()
                            .map(Purchase::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return Mono.just(PurchaseSummaryTO.builder()
                            .startDate(date)
                            .endDate(date)
                            .totalAmount(total)
                            .purchaseCount((long) purchases.size())
                            .period("DAY")
                            .build());
                });
    }

    @Override
    public Mono<PurchaseSummaryTO> getPurchaseSummaryByWeek(LocalDate date, Long userId) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return purchaseRepository.findByUserIdAndPurchaseDateBetween(userId, startOfWeek, endOfWeek)
                .collectList()
                .flatMap(purchases -> {
                    BigDecimal total = purchases.stream()
                            .map(Purchase::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return Mono.just(PurchaseSummaryTO.builder()
                            .startDate(startOfWeek)
                            .endDate(endOfWeek)
                            .totalAmount(total)
                            .purchaseCount((long) purchases.size())
                            .period("WEEK")
                            .build());
                });
    }

    @Override
    public Mono<PurchaseSummaryTO> getPurchaseSummaryByMonth(int year, int month, Long userId) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        return purchaseRepository.findByUserIdAndPurchaseDateBetween(userId, startOfMonth, endOfMonth)
                .collectList()
                .flatMap(purchases -> {
                    BigDecimal total = purchases.stream()
                            .map(Purchase::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return Mono.just(PurchaseSummaryTO.builder()
                            .startDate(startOfMonth)
                            .endDate(endOfMonth)
                            .totalAmount(total)
                            .purchaseCount((long) purchases.size())
                            .period("MONTH")
                            .build());
                });
    }

    @Override
    public Mono<PurchaseSummaryTO> getPurchaseSummaryByYear(int year, Long userId) {
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);

        return purchaseRepository.findByUserIdAndPurchaseDateBetween(userId, startOfYear, endOfYear)
                .collectList()
                .flatMap(purchases -> {
                    BigDecimal total = purchases.stream()
                            .map(Purchase::getTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return Mono.just(PurchaseSummaryTO.builder()
                            .startDate(startOfYear)
                            .endDate(endOfYear)
                            .totalAmount(total)
                            .purchaseCount((long) purchases.size())
                            .period("YEAR")
                            .build());
                });
    }

    private Mono<PurchaseTO> mapToDTO(Purchase purchase) {
        Mono<String> categoryName = Mono.justOrEmpty(purchase.getCategory())
                .flatMap(category -> categoryRepository.findById(category.getId()))
                .map(Category::getName)
                .defaultIfEmpty("");

        return categoryName.map(name -> PurchaseTO.builder()
                .id(purchase.getId())
                .purchaseDate(purchase.getPurchaseDate())
                .merchant(purchase.getMerchant())
                .merchantAddress(purchase.getMerchantAddress())
                .price(purchase.getPrice())
                .vat(purchase.getVat())
                .total(purchase.getTotal())
                .quantity(purchase.getQuantity())
                .link(purchase.getLink())
                .categoryId(purchase.getCategory() != null ? purchase.getCategory().getId() : null)
                .categoryName(name)
                .userId(purchase.getUser() != null ? purchase.getUser().getId() : null)
                .build());
    }
}
