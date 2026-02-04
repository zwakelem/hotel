package za.co.simplitate.hotelbooking.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.PurchaseSummaryTO;
import za.co.simplitate.hotelbooking.dtos.PurchaseTO;

import java.time.LocalDate;

public interface PurchaseService {
    
    Mono<PurchaseTO> createPurchase(PurchaseTO purchaseTO, Long userId);
    
    Mono<PurchaseTO> updatePurchase(Long id, PurchaseTO purchaseTO, Long userId);
    
    Mono<Void> deletePurchase(Long id, Long userId);
    
    Mono<PurchaseTO> getPurchaseById(Long id, Long userId);
    
    Flux<PurchaseTO> getAllPurchasesByUser(Long userId);
    
    Flux<PurchaseTO> getPurchasesByCategory(Long categoryId, Long userId);
    
    Flux<PurchaseTO> getPurchasesByDateRange(LocalDate startDate, LocalDate endDate, Long userId);
    
    Mono<PurchaseSummaryTO> getPurchaseSummaryByDay(LocalDate date, Long userId);
    
    Mono<PurchaseSummaryTO> getPurchaseSummaryByWeek(LocalDate date, Long userId);
    
    Mono<PurchaseSummaryTO> getPurchaseSummaryByMonth(int year, int month, Long userId);
    
    Mono<PurchaseSummaryTO> getPurchaseSummaryByYear(int year, Long userId);
}
