package com.autobots.automanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;
import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permite todas as rotas
                .allowedOrigins("http://localhost:8081") // Adicione a URL do seu frontend ou domínio
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Métodos permitidos
                .allowedHeaders("*"); // Todos os cabeçalhos permitidos
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Criação do ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Registra o módulo para Java 8 Time (LocalDate, LocalDateTime, etc.)
        objectMapper.registerModule(new JavaTimeModule());

        // Define o formato de data global
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        // Desativa a falha em caso de beans vazios
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // Adiciona o conversor de mensagens
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    }
}
