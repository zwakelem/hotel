package za.co.simplitate.hotelbooking.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.BudgetStatusTO;
import za.co.simplitate.hotelbooking.dtos.BudgetTO;
import za.co.simplitate.hotelbooking.util.enums.BudgetPeriod;

public interface BudgetService {
    
    Mono<BudgetTO> createBudget(BudgetTO budgetTO, Long userId);
    
    Mono<BudgetTO> updateBudget(Long id, BudgetTO budgetTO, Long userId);
    
    Mono<Void> deleteBudget(Long id, Long userId);
    
    Mono<BudgetTO> getBudgetById(Long id, Long userId);
    
    Flux<BudgetTO> getAllBudgetsByUser(Long userId);
    
    Flux<BudgetTO> getBudgetsByPeriod(BudgetPeriod period, Long userId);
    
    Mono<BudgetStatusTO> getBudgetStatus(Long categoryId, BudgetPeriod period, Long userId);
    
    Flux<BudgetStatusTO> getAllBudgetStatuses(BudgetPeriod period, Long userId);
}
