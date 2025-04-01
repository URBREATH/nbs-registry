package gr.atc.urbreath;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class UbreathNbsRegistryApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertNotNull(ApplicationContext.class);
	}

}
