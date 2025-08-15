package kr.ai.boomerang.boomerang_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 부메랑(Boomerang)
 *
 * @author Boomerang
 * @version 1.0
 */

@SpringBootApplication
@EnableJpaAuditing
public class BoomerangSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoomerangSpringBootApplication.class, args);
	}
}