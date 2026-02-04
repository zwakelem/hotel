package za.co.simplitate.hotelbooking.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.dtos.CategoryTO;
import za.co.simplitate.hotelbooking.entities.Category;
import za.co.simplitate.hotelbooking.entities.User;
import za.co.simplitate.hotelbooking.entities.repositories.CategoryRepository;
import za.co.simplitate.hotelbooking.services.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Mono<CategoryTO> createCategory(CategoryTO categoryTO, Long userId) {
        Category category = Category.builder()
                .name(categoryTO.getName())
                .description(categoryTO.getDescription())
                .user(User.builder().id(userId).build())
                .build();

        return categoryRepository.save(category)
                .map(this::mapToDTO);
    }

    @Override
    public Mono<CategoryTO> updateCategory(Long id, CategoryTO categoryTO, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .flatMap(existingCategory -> {
                    existingCategory.setName(categoryTO.getName());
                    existingCategory.setDescription(categoryTO.getDescription());
                    return categoryRepository.save(existingCategory);
                })
                .map(this::mapToDTO);
    }

    @Override
    public Mono<Void> deleteCategory(Long id, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .flatMap(categoryRepository::delete);
    }

    @Override
    public Mono<CategoryTO> getCategoryById(Long id, Long userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .map(this::mapToDTO);
    }

    @Override
    public Flux<CategoryTO> getAllCategoriesByUser(Long userId) {
        return categoryRepository.findByUserId(userId)
                .map(this::mapToDTO);
    }

    private CategoryTO mapToDTO(Category category) {
        return CategoryTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .userId(category.getUser() != null ? category.getUser().getId() : null)
                .build();
    }
}
