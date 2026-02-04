package za.co.simplitate.hotelbooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "za.co.simplitate.hotelbooking.entities.repositories")
public class R2dbcConfig {
}
