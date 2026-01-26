package bragdoc.infrastructure.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;

public final class DotenvLoader {

    private static final Logger log = LoggerFactory.getLogger(DotenvLoader.class);

    private DotenvLoader() {
    }

    public static void load() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries()
                    .forEach(e -> System.setProperty(e.getKey(), e.getValue()));
        } catch (Exception e) {
            log.warn("Não foi possível carregar o arquivo .env: {}", e.getMessage());
        }
    }
}
