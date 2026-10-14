package com.glossup.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Metadatos de la documentacion OpenAPI/Swagger del backend GlossUP. */
@Configuration
public class OpenApiConfig {

    /** Nombre del esquema de seguridad JWT referenciado por los controladores (@SecurityRequirement). */
    public static final String ESQUEMA_JWT = "bearer-jwt";

    @Bean
    public OpenAPI glossupOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GlossUP API")
                        .description("API del ecommerce de cosmetica con motor de compatibilidad dermatologica")
                        .version("v0.0.1")
                        .contact(new Contact().name("Equipo GlossUP - Universidad del Quindio"))
                        .license(new License().name("Uso academico")))
                .components(new Components().addSecuritySchemes(ESQUEMA_JWT,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido en el login (RNF-11)")));
    }
}
