package bragdoc.interfaces.webconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BragDoc API")
                        .description("API para integração com GitHub e gerenciamento de conquistas")
                        .version("v1")
                        .contact(new Contact()
                                .name("BragDoc")));
    }
}
