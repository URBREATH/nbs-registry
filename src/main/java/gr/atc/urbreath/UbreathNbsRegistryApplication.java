package gr.atc.urbreath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class UbreathNbsRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(UbreathNbsRegistryApplication.class, args);
	}

}
