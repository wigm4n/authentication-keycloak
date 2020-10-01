package ru.wigm4n.jwt.auth.keycloak.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URI;
import java.net.URISyntaxException;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TaJwtProviderSettings {

    private final URI uri;
    private final String clientId;
    private final String clientSecret;
    private final Long tokenExpDelta;
    private final Long tokenRefreshDelta;
    private final Long tokenRefreshDelayMin;
    private final String authProvider;

    public static TaJwtProviderSettings.Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private URI uri;
        private String clientId;
        private String clientSecret;
        private Long tokenExpDelta;
        private Long tokenRefreshDelta;
        private Long tokenRefreshDelayMin;
        private String authProvider;

        public Builder setURI(String uri) {
            try {
                this.uri = new URI(uri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }

            return this;
        }

        public Builder setAuthProvider(String authProvider) {
            this.authProvider = authProvider;

            return this;
        }

        public Builder setClientId(String clientId) {
            if (clientId == null || clientId.isEmpty()) {
                throw new IllegalArgumentException();
            }

            this.clientId = clientId;

            return this;
        }

        public Builder setClientSecret(String clientSecret) {
            if (clientSecret == null || clientSecret.isEmpty()) {
                throw new IllegalArgumentException();
            }

            this.clientSecret = clientSecret;

            return this;
        }

        public Builder setTokenExpDelta(Long tokenExpDelta) {
            this.tokenExpDelta = tokenExpDelta;

            return this;
        }

        public Builder setTokenRefreshDelta(Long tokenRefreshDelta) {
            this.tokenRefreshDelta = tokenRefreshDelta;

            return this;
        }

        public Builder setTokenRefreshDelayMin(Long tokenRefreshDelayMin) {
            this.tokenRefreshDelayMin = tokenRefreshDelayMin;

            return this;
        }

        public TaJwtProviderSettings build() {
            if (clientId == null) {
                throw new IllegalStateException("client_id should be specified");
            }

            if (clientSecret == null) {
                throw new IllegalStateException("client_secret should be specified");
            }

            if (uri == null) {
                throw new IllegalStateException("uri should be specified");
            }

            if (authProvider == null) {
                authProvider = "basic";
            }

            if (tokenExpDelta == null) {
                tokenExpDelta = 1L;
            }

            if (tokenRefreshDelta == null) {
                tokenRefreshDelta = Math.max(1L, tokenExpDelta);
            }

            if (tokenRefreshDelayMin == null) {
                tokenRefreshDelayMin = 1L;
            }

            return new TaJwtProviderSettings(uri, clientId, clientSecret, tokenExpDelta, tokenRefreshDelta,
                tokenRefreshDelayMin, authProvider);
        }
    }
}
