package com.boraver.teamgenerator.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Team Generator API")
            .description("API do aplicativo TeamGenerator")
            .version("1.0.0")
            .contact(new Contact()
                .name("Suporte V M MELO")
                .email("valmom@gmail.com")
                .url("https://teamgenerator.com"))
            .license(new License()
                .name("Licença de Uso")
                .url("https://teamgenerator.com/licenca")));
  }
}