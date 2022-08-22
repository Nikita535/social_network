package sosal_network;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class SosalNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SosalNetworkApplication.class, args);
	}

}
