package com.glossup.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Metadatos de la documentacion OpenAPI/Swagger del backend GlossUP. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI glossupOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("GlossUP API")
                .description("API del ecommerce de cosmetica con motor de compatibilidad dermatologica")
                .version("v0.0.1")
                .contact(new Contact().name("Equipo GlossUP - Universidad del Quindio"))
                .license(new License().name("Uso academico")));
    }
}
