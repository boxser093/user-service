package net.ilya.users_api_microservice_on_webflux.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "User service gateway.",
                description = "This service is needed for use between microservices inside a closed system, there is no direct access to it.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Ilya Predvechnyy",
                        email = "foodev@example.dev",
                        url = "https://foo.dev.biz"
                )
        )
)

@Configuration
public class OpenApiConfig {

}
