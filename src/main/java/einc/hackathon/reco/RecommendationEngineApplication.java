package einc.hackathon.reco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "einc.hackathon.reco")
@EnableJpaRepositories
public class RecommendationEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecommendationEngineApplication.class, args);
	}

}
