package ru.wigm4n.jwt.auth.keycloak.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import ru.wigm4n.jwt.auth.keycloak.cfg.TaSettings;

import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfiguration {

    private final RestTemplateBuilder restTemplateBuilder;
    private final TaSettings taSettings;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.getInterceptors().add(getInterceptor());

        return restTemplate;
    }

    private ClientHttpRequestInterceptor getInterceptor() {
        return ((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Basic " + encodeCredentialsForBasicAuth());
            return execution.execute(request, body);
        });
    }

    private String encodeCredentialsForBasicAuth() {
        return Base64.getEncoder()
                     .encodeToString((taSettings.getUsername() + ":" + taSettings.getPassword()).getBytes());
    }
}
