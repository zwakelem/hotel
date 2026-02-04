package za.co.simplitate.hotelbooking.entities.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.entities.Budget;
import za.co.simplitate.hotelbooking.util.enums.BudgetPeriod;

@Repository
public interface BudgetRepository extends ReactiveCrudRepository<Budget, Long> {

    Flux<Budget> findByUserId(Long userId);

    Mono<Budget> findByIdAndUserId(Long id, Long userId);

    Mono<Budget> findByUserIdAndCategoryIdAndPeriod(Long userId, Long categoryId, BudgetPeriod period);

    Flux<Budget> findByUserIdAndPeriod(Long userId, BudgetPeriod period);
}
