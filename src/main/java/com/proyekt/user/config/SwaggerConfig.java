package com.proyekt.user.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // API-nin ümumi məlumatları - Swagger səhifəsinin yuxarısında görünür
                .info(new Info()
                        .title("Todo List API")           // API-nin adı
                        .version("1.0")                   // API-nin versiyası
                        .description("Todo List tətbiqinin API dokumentasiyası") // Təsvir
                        .contact(new Contact()
                                .name("Seymur")           // Developer adı
                                .email("seymur@email.com"))) // Developer emaili

                // Swagger-ə "bu API JWT token tələb edir" deyirik
                // Bunun sayəsində Swagger-də "Authorize" düyməsi çıxır
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))

                // Token-in necə işləyəcəyini təyin edirik
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)  // HTTP üzərindən
                                        .scheme("bearer")                // Bearer formatında
                                        .bearerFormat("JWT")));          // JWT token istifadə edilir
    }
}
