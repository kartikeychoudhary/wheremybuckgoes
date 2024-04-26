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
