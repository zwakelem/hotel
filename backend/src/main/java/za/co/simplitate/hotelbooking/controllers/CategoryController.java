package za.co.simplitate.hotelbooking.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.CategoryTO;
import za.co.simplitate.hotelbooking.dtos.Response;
import za.co.simplitate.hotelbooking.services.CategoryService;
import za.co.simplitate.hotelbooking.services.security.AuthUser;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Mono<Response> createCategory(@RequestBody CategoryTO categoryTO, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return categoryService.createCategory(categoryTO, userId)
                .map(category -> Response.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Category created successfully")
                        .category(category)
                        .build());
    }

    @PutMapping("/{id}")
    public Mono<Response> updateCategory(@PathVariable Long id, @RequestBody CategoryTO categoryTO, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return categoryService.updateCategory(id, categoryTO, userId)
                .map(category -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Category updated successfully")
                        .category(category)
                        .build());
    }

    @DeleteMapping("/{id}")
    public Mono<Response> deleteCategory(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return categoryService.deleteCategory(id, userId)
                .then(Mono.just(Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Category deleted successfully")
                        .build()));
    }

    @GetMapping("/{id}")
    public Mono<Response> getCategoryById(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return categoryService.getCategoryById(id, userId)
                .map(category -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Category retrieved successfully")
                        .category(category)
                        .build());
    }

    @GetMapping
    public Mono<Response> getAllCategories(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        return categoryService.getAllCategoriesByUser(userId)
                .collectList()
                .map(categories -> Response.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Categories retrieved successfully")
                        .categoryList(categories)
                        .build());
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return authUser.getId();
    }
}
