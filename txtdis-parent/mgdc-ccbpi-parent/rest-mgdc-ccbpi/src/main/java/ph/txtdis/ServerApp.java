package ph.txtdis;

import org.springframework.boot.SpringApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class ServerApp {

	public static void main(String[] args) {
		System.setProperty("jasypt.encryptor.password", "I'mAdmin4txtDIS@PostgreSQL");
		SpringApplication.run(ServerApp.class);
	}
}