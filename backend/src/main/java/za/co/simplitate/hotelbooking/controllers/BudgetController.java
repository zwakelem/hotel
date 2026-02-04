package za.co.simplitate.hotelbooking.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.BudgetTO;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.services.BudgetService;
import za.co.simplitate.hotelbooking.services.security.AuthUser;
import za.co.simplitate.hotelbooking.util.enums.BudgetPeriod;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public Mono<Response> createBudget(@RequestBody BudgetTO budgetTO, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return budgetService.createBudget(budgetTO, userId)
                .map(budget -> Response.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Budget created successfully")
                        .budget(budget)
                        .build());
    }

    @PutMapping("/{id}")
    public Mono<Response> updateBudget(@PathVariable Long id, @RequestBody BudgetTO budgetTO, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return budgetService.updateBudget(id, budgetTO, userId)
                .map(budget -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Budget updated successfully")
                        .budget(budget)
                        .build());
    }

    @DeleteMapping("/{id}")
    public Mono<Response> deleteBudget(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return budgetService.deleteBudget(id, userId)
                .then(Mono.just(Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Budget deleted successfully")
                        .build()));
    }

    @GetMapping("/{id}")
    public Mono<Response> getBudgetById(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return budgetService.getBudgetById(id, userId)
                .map(budget -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Budget retrieved successfully")
                        .budget(budget)
                        .build());
    }

    @GetMapping
    public Mono<Response> getAllBudgets(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return budgetService.getAllBudgetsByUser(userId)
                .collectList()
                .map(budgets -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Budgets retrieved successfully")
                        .budgetList(budgets)
                        .build());
    }

    @GetMapping("/period/{period}")
    public Mono<Response> getBudgetsByPeriod(@PathVariable BudgetPeriod period, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return budgetService.getBudgetsByPeriod(period, userId)
                .collectList()
                .map(budgets -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Budgets retrieved successfully")
                        .budgetList(budgets)
                        .build());
    }

    @GetMapping("/status/category/{categoryId}/period/{period}")
    public Mono<Response> getBudgetStatus(
            @PathVariable Long categoryId,
            @PathVariable BudgetPeriod period,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return budgetService.getBudgetStatus(categoryId, period, userId)
                .map(status -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Budget status retrieved successfully")
                        .budgetStatus(status)
                        .build());
    }

    @GetMapping("/status/period/{period}")
    public Mono<Response> getAllBudgetStatuses(@PathVariable BudgetPeriod period, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return budgetService.getAllBudgetStatuses(period, userId)
                .collectList()
                .map(statuses -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Budget statuses retrieved successfully")
                        .budgetStatusList(statuses)
                        .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return authUser.getId();
    }
}
