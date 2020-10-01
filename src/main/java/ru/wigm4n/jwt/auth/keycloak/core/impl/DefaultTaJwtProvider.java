package ru.wigm4n.jwt.auth.keycloak.core.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.wigm4n.jwt.auth.keycloak.core.internal.JwtTokenPair;
import ru.wigm4n.jwt.auth.keycloak.exception.GetJwtException;
import ru.wigm4n.jwt.auth.keycloak.rest.RestKeycloakClient;
import ru.wigm4n.jwt.auth.keycloak.rest.RestTemplateConfiguration;
import ru.wigm4n.jwt.auth.keycloak.rest.dto.JwtRequest;
import ru.wigm4n.jwt.auth.keycloak.cfg.TaJwtProviderSettings;
import ru.wigm4n.jwt.auth.keycloak.cfg.TaSettings;
import ru.wigm4n.jwt.auth.keycloak.core.Jwt;
import ru.wigm4n.jwt.auth.keycloak.core.JwtValidator;
import ru.wigm4n.jwt.auth.keycloak.core.TaJwtProvider;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Slf4j
public class DefaultTaJwtProvider implements TaJwtProvider {

    public DefaultTaJwtProvider(TaJwtProviderSettings config, JwtValidator jwtValidator,
                                RestTemplateConfiguration restTemplateConfiguration, TaSettings taSettings) {
        this.config = config;
        this.client = new RestKeycloakClient(jwtValidator, restTemplateConfiguration, taSettings, config.getUri());
    }

    private final TaJwtProviderSettings config;
    private final RestKeycloakClient client;
    private final ReentrantLock reentrantLock = new ReentrantLock();

    volatile private JwtTokenPair tokenPair;

    @Override
    public Jwt getJwtToken() {
        return getJwtToken(false);
    }

    public Jwt getJwtToken(boolean forceRefresh) {
        JwtTokenPair current = tokenPair;

        if (!forceRefresh && current != null && tokenNotExpired(current.getAccessToken(), config.getTokenExpDelta())) {
            return current.getAccessToken();
        }

        reentrantLock.lock();
        try {
            JwtTokenPair newPair = null;

            if (current != null && current.getRefreshToken() != null && tokenNotExpired(current.getRefreshToken(),
                config.getTokenExpDelta())) {

                try {
                    JwtRequest request = buildRefreshRequest(config, current.getRefreshToken());
                    newPair = client.refreshToken(request);
                    log.info("JWT TA token refreshed successfully");
                } catch (GetJwtException e) {
                    log.warn("Unable to refresh JWT TA token, will try to issue new one", e);
                }
            }

            if (newPair == null) {
                JwtRequest request = buildIssueRequest(config);
                newPair = client.getToken(request);
                log.info("JWT TA token issued successfully");
            }

            tokenPair = newPair;

            if (newPair.getRefreshToken() == null) {
                log.info("Got new JWT TA pair WITHOUT refresh token.");
            }

        } finally {
            reentrantLock.unlock();
        }

        return tokenPair.getAccessToken();
    }

    private boolean tokenNotExpired(Jwt token, Long tokenExpDelta) {
        return getTokenTTLMs(token, tokenExpDelta) > 0;
    }

    private long getTokenTTLMs(Jwt token, Long tokenExpDeltaSec) {
        Optional<Long> expSeconds = token.getClaimAs("exp", Long.class);
        if (expSeconds.isEmpty()) {
            throw new IllegalStateException("Decoded token has not expired date.");
        }
        return (expSeconds.get() - tokenExpDeltaSec) * 1000 - System.currentTimeMillis();
    }

    private JwtRequest buildRefreshRequest(TaJwtProviderSettings settings, Jwt refreshToken) {
        return JwtRequest.builder()
                         .withClientId(settings.getClientId())
                         .withClientSecret(settings.getClientSecret())
                         .withGrantType(JwtRequest.GRANT_TYPE_REFRESH_TOKEN)
                         .withRefreshToken(refreshToken.toString())
                         .build();
    }

    private JwtRequest buildIssueRequest(TaJwtProviderSettings settings) {
        return JwtRequest.builder()
                         .withClientId(settings.getClientId())
                         .withClientSecret(settings.getClientSecret())
                         .build();
    }
}
