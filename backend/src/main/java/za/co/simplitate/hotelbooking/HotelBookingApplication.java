package za.co.simplitate.hotelbooking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;

@EnableAsync
@EnableCaching
@EnableJpaRepositories
@SpringBootApplication
@Slf4j
public class HotelBookingApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(HotelBookingApplication.class, args);

        Environment env = ctx.getEnvironment();
        log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));

        log.info("---- System environment variables ----");
        System.getenv().forEach((k, v) -> log.info("{}={}", k, v));

        log.info("---- AWS property values ----");
        log.info("aws.s3.bucket-name={}", env.getProperty("aws.s3.bucket-name"));
        log.info("aws.s3.bucket-arn={}", env.getProperty("aws.s3.bucket-arn"));
        log.info("aws.s3.region={}", env.getProperty("aws.s3.region"));
        log.info("aws.s3.bucket-url={}", env.getProperty("aws.s3.bucket-url"));
        log.info("aws.s3.bucket-images-folder={}", env.getProperty("aws.s3.bucket-images-folder"));

        log.info("---- database property values ----");
        log.info("spring.datasource.url={}", env.getProperty("spring.datasource.url"));
        log.info("spring.datasource.username={}", env.getProperty("spring.datasource.username"));
	}

}
