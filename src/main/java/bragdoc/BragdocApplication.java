package bragdoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BragdocApplication {

	public static void main(String[] args) {
		// Carregar variáveis do arquivo .env
		try {
			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.load();

			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		} catch (Exception e) {
			System.out.println("Arquivo .env não encontrado, usando variáveis de ambiente do sistema");
		}

		SpringApplication.run(BragdocApplication.class, args);
	}

}
