package com.vs.prompt.manager.web.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Prompt Manager API",
                version = "1.0",
                description = "API for managing prompts and related categories"
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        }
)
@Configuration
public class OpenApiConfig {
}
