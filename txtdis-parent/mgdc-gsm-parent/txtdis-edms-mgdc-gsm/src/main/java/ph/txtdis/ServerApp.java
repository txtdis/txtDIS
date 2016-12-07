package ph.txtdis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@PropertySource("edms.properties")
@PropertySource("application.properties")
public class ServerApp {

	public static void main(String[] args) {
		SpringApplication.run(ServerApp.class);
	}
}