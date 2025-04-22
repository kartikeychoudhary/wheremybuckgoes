/**
 * Main application class for WhereMyBuckGoes expense tracking system.
 * <p>
 * This class serves as the entry point for the WhereMyBuckGoes application,
 * a Spring Boot-based backend service for personal finance management.
 * The application provides APIs for tracking expenses, categorizing transactions,
 * and visualizing spending patterns.
 * <p>
 * The class is annotated with:
 * - @SpringBootApplication: Combines @Configuration, @EnableAutoConfiguration,
 *   and @ComponentScan annotations for Spring Boot application setup
 * - @EnableScheduling: Enables Spring's scheduled task execution capability
 *   for background processing of tasks like GenAI requests and notifications
 * - @EnableEncryptableProperties: Enables Jasypt encryption for sensitive
 *   configuration properties in application.yml
 *
 * @author Kartikey Choudhary (kartikey31choudhary@gmail.com)
 * @version 0.1
 * @since 2024
 */

package kc.wheremybuckgoes;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableEncryptableProperties
public class WhereMyBuckGoesApplication {
	public static void main(String[] args) {
		SpringApplication.run(WhereMyBuckGoesApplication.class, args);
	}

}
