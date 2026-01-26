package bragdoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import bragdoc.infrastructure.bootstrap.DotenvLoader;

@SpringBootApplication
public class BragdocApplication {

	public static void main(String[] args) {
		DotenvLoader.load();
		SpringApplication.run(BragdocApplication.class, args);
	}

}
