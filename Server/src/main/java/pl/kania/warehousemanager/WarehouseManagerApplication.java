package pl.kania.warehousemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class WarehouseManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarehouseManagerApplication.class, args);
	}

}
