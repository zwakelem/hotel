package za.co.simplitate.hotelbooking.entities.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import za.co.simplitate.hotelbooking.entities.Category;

@Repository
public interface CategoryRepository extends ReactiveCrudRepository<Category, Long> {

    Flux<Category> findByUserId(Long userId);

    Mono<Category> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT * FROM category WHERE user_id = :userId AND name = :name")
    Mono<Category> findByUserIdAndName(Long userId, String name);
}
