package za.co.simplitate.hotelbooking.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.CategoryTO;

public interface CategoryService {
    
    Mono<CategoryTO> createCategory(CategoryTO categoryTO, Long userId);
    
    Mono<CategoryTO> updateCategory(Long id, CategoryTO categoryTO, Long userId);
    
    Mono<Void> deleteCategory(Long id, Long userId);
    
    Mono<CategoryTO> getCategoryById(Long id, Long userId);
    
    Flux<CategoryTO> getAllCategoriesByUser(Long userId);
}
