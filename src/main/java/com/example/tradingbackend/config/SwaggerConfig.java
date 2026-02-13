package com.example.tradingbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Trading Platform Support");
        contact.setEmail("support@trading.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Trading Platform API")
                .version("1.0.0")
                .description("REST API for Trading Platform - User Management and Trading Operations")
                .contact(contact)
                .license(license);

        return new OpenAPI().info(info);
    }
}
