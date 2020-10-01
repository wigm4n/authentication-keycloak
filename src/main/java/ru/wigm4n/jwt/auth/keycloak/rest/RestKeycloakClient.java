package ru.wigm4n.jwt.auth.keycloak.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.wigm4n.jwt.auth.keycloak.configuration.KeycloakConfigurationProperties;
import ru.wigm4n.jwt.auth.keycloak.core.JwtValidator;
import ru.wigm4n.jwt.auth.keycloak.core.internal.JwtTokenPair;
import ru.wigm4n.jwt.auth.keycloak.exception.GetJwtException;
import ru.wigm4n.jwt.auth.keycloak.rest.dto.JwtRequest;
import ru.wigm4n.jwt.auth.keycloak.rest.dto.JwtResponse;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class RestKeycloakClient {

    private final JwtValidator jwtValidator;
    private final RestTemplateConfiguration configuration;
    private final KeycloakConfigurationProperties properties;
    private final URI endpoint;

    public JwtTokenPair getToken(JwtRequest request) {
        return doJwtRequest(configuration.restTemplate(), request, (e) -> {
            throw new GetJwtException("Failed to get new JWT token", e);
        });
    }

    public JwtTokenPair refreshToken(JwtRequest request) {
        return doJwtRequest(configuration.restTemplate(), request, (e) -> {
            throw new GetJwtException("Failed to refresh JWT token", e);
        });
    }

    private JwtTokenPair doJwtRequest(RestTemplate restTemplate, JwtRequest request,
                                      Consumer<RestClientException> exceptionHandler) throws RestClientException {
        try {
            RequestEntity requestEntity = RequestEntity
                .post(endpoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(buildRequestFormDataString(request));
            JwtResponse jwtResponse = restTemplate.exchange(requestEntity, JwtResponse.class).getBody();

            if (jwtResponse == null) {
                return new JwtTokenPair(null, null);
            }

            var accessToken = jwtValidator.decode(jwtResponse.getAccessToken());
            var refreshToken = jwtResponse.getRefreshToken() != null ? jwtValidator.decode(
                jwtResponse.getRefreshToken()) : null;

            return new JwtTokenPair(accessToken, refreshToken);

        } catch (RestClientException e) {
            exceptionHandler.accept(e);
            throw new GetJwtException("Error while calling JWT endpoint", e);
        }
    }

    private String buildRequestFormDataString(JwtRequest request) {
        StringBuilder builder = new StringBuilder();
        builder
            .append("grant_type=")
            .append(encode(request.getGrantType()))
            .append("&client_id=")
            .append(encode(request.getClientId()))
            .append("&client_secret=")
            .append(encode(request.getClientSecret()))
            .append("&username=")
            .append(encode(properties.getTaUsername()))
            .append("&password=")
            .append(encode(properties.getTaPassword()));

        if (request.getRefreshToken() != null) {
            builder.append("&refresh_token=")
                   .append(encode(request.getRefreshToken()));
        }

        return builder.toString();
    }

    private static String encode(String param) {
        try {
            return param != null ? URLEncoder.encode(param, StandardCharsets.UTF_8.name()) : null;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode request parameter", e);
        }
    }
}
