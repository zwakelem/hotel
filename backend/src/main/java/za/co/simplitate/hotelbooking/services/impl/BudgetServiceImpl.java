package za.co.simplitate.hotelbooking.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.BudgetStatusTO;
import za.co.simplitate.hotelbooking.dtos.BudgetTO;
import za.co.simplitate.hotelbooking.entities.Budget;
import za.co.simplitate.hotelbooking.entities.Category;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.entities.repositories.BudgetRepository;
import za.co.simplitate.hotelbooking.entities.repositories.CategoryRepository;
import za.co.simplitate.hotelbooking.entities.repositories.PurchaseRepository;
import za.co.simplitate.hotelbooking.services.BudgetService;
import za.co.simplitate.hotelbooking.util.enums.BudgetPeriod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final PurchaseRepository purchaseRepository;

    @Override
    public Mono<BudgetTO> createBudget(BudgetTO budgetTO, Long userId) {
        Budget budget = Budget.builder()
                .category(Category.builder().id(budgetTO.getCategoryId()).build())
                .amount(budgetTO.getAmount())
                .period(budgetTO.getPeriod())
                .user(User.builder().id(userId).build())
                .build();

        return budgetRepository.save(budget)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Mono<BudgetTO> updateBudget(Long id, BudgetTO budgetTO, Long userId) {
        return budgetRepository.findByIdAndUserId(id, userId)
                .flatMap(existingBudget -> {
                    existingBudget.setAmount(budgetTO.getAmount());
                    existingBudget.setPeriod(budgetTO.getPeriod());
                    if (budgetTO.getCategoryId() != null) {
                        existingBudget.setCategory(Category.builder().id(budgetTO.getCategoryId()).build());
                    }
                    return budgetRepository.save(existingBudget);
                })
                .flatMap(this::mapToDTO);
    }

    @Override
    public Mono<Void> deleteBudget(Long id, Long userId) {
        return budgetRepository.findByIdAndUserId(id, userId)
                .flatMap(budgetRepository::delete);
    }

    @Override
    public Mono<BudgetTO> getBudgetById(Long id, Long userId) {
        return budgetRepository.findByIdAndUserId(id, userId)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Flux<BudgetTO> getAllBudgetsByUser(Long userId) {
        return budgetRepository.findByUserId(userId)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Flux<BudgetTO> getBudgetsByPeriod(BudgetPeriod period, Long userId) {
        return budgetRepository.findByUserIdAndPeriod(userId, period)
                .flatMap(this::mapToDTO);
    }

    @Override
    public Mono<BudgetStatusTO> getBudgetStatus(Long categoryId, BudgetPeriod period, Long userId) {
        return budgetRepository.findByUserIdAndCategoryIdAndPeriod(userId, categoryId, period)
                .flatMap(budget -> {
                    LocalDate[] dateRange = getDateRangeForPeriod(period);
                    LocalDate startDate = dateRange[0];
                    LocalDate endDate = dateRange[1];

                    return purchaseRepository.sumTotalByUserIdAndCategoryIdAndDateBetween(userId, categoryId, startDate, endDate)
                            .defaultIfEmpty(BigDecimal.ZERO)
                            .flatMap(spentAmount -> categoryRepository.findById(categoryId)
                                    .map(category -> {
                                        BigDecimal remaining = budget.getAmount().subtract(spentAmount);
                                        BigDecimal percentageUsed = BigDecimal.ZERO;
                                        if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                                            percentageUsed = spentAmount
                                                    .divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                                                    .multiply(BigDecimal.valueOf(100))
                                                    .setScale(2, RoundingMode.HALF_UP);
                                        }

                                        return BudgetStatusTO.builder()
                                                .categoryId(categoryId)
                                                .categoryName(category.getName())
                                                .budgetAmount(budget.getAmount())
                                                .spentAmount(spentAmount)
                                                .remainingAmount(remaining)
                                                .period(period)
                                                .percentageUsed(percentageUsed)
                                                .build();
                                    })
                            );
                });
    }

    @Override
    public Flux<BudgetStatusTO> getAllBudgetStatuses(BudgetPeriod period, Long userId) {
        return budgetRepository.findByUserIdAndPeriod(userId, period)
                .flatMap(budget -> {
                    Long categoryId = budget.getCategory().getId();
                    return getBudgetStatus(categoryId, period, userId);
                });
    }

    private LocalDate[] getDateRangeForPeriod(BudgetPeriod period) {
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        switch (period) {
            case DAILY:
                startDate = today;
                endDate = today;
                break;
            case WEEKLY:
                startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case MONTHLY:
                startDate = today.with(TemporalAdjusters.firstDayOfMonth());
                endDate = today.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case YEARLY:
                startDate = LocalDate.of(today.getYear(), 1, 1);
                endDate = LocalDate.of(today.getYear(), 12, 31);
                break;
            default:
                startDate = today;
                endDate = today;
        }

        return new LocalDate[]{startDate, endDate};
    }

    private Mono<BudgetTO> mapToDTO(Budget budget) {
        return categoryRepository.findById(budget.getCategory().getId())
                .map(category -> BudgetTO.builder()
                        .id(budget.getId())
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .amount(budget.getAmount())
                        .period(budget.getPeriod())
                        .userId(budget.getUser() != null ? budget.getUser().getId() : null)
                        .build())
                .switchIfEmpty(Mono.just(BudgetTO.builder()
                        .id(budget.getId())
                        .categoryId(budget.getCategory().getId())
                        .categoryName("")
                        .amount(budget.getAmount())
                        .period(budget.getPeriod())
                        .userId(budget.getUser() != null ? budget.getUser().getId() : null)
                        .build()));
    }
}
