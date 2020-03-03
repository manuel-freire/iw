package es.ucm.fdi.iw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement	
public class IwApplication {

	public static void main(String[] args) {
		SpringApplication.run(IwApplication.class, args);
	}
}
