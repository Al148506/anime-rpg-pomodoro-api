package com.alexdev.animerpgpomodoro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        Server productionServer = new Server();

        productionServer.setUrl(
                "https://anime-rpg-pomodoro-api-production.up.railway.app"
        );

        productionServer.setDescription("Production Server");

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Anime RPG Pomodoro API")
                                .version("1.0")
                                .description(
                                        "REST API for Anime RPG Pomodoro application"
                                )
                )
                .servers(List.of(productionServer));
    }
}