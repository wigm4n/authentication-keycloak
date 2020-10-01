package ru.wigm4n.jwt.auth.keycloak.rest.dto;

import lombok.Data;

@Data
public class JwtRequest {

    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;

    private JwtRequest(String grantType, String clientId, String clientSecret, String refreshToken) {
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String grantType;
        private String clientId;
        private String clientSecret;
        private String refreshToken;

        public JwtRequest build() {

            if (grantType == null) {
                grantType = GRANT_TYPE_PASSWORD;
            }

            if (clientId == null) {
                throw new IllegalArgumentException("client_id should be specified");
            }

            if (clientSecret == null) {
                throw new IllegalArgumentException("client_secret should be specified");
            }

            if (GRANT_TYPE_REFRESH_TOKEN.equals(grantType) && refreshToken == null) {
                throw new IllegalArgumentException(
                    "refresh_token should be specified for grant_type '" + GRANT_TYPE_REFRESH_TOKEN + "'");
            }

            if (!GRANT_TYPE_REFRESH_TOKEN.equals(grantType) && refreshToken != null) {
                throw new IllegalArgumentException(
                    "refresh_token should be used only with grant_type '" + GRANT_TYPE_REFRESH_TOKEN + "'");
            }

            return new JwtRequest(grantType, clientId, clientSecret, refreshToken);
        }

        public Builder withGrantType(String grantType) {
            if (grantType == null || grantType.isEmpty()) {
                throw new IllegalArgumentException();
            }

            this.grantType = grantType;

            return this;
        }

        public Builder withRefreshToken(String refreshToken) {
            if (refreshToken == null || refreshToken.isEmpty()) {
                throw new IllegalArgumentException();
            }

            this.refreshToken = refreshToken;

            return this;
        }

        public Builder withClientId(String clientId) {
            if (clientId == null || clientId.isEmpty()) {
                throw new IllegalArgumentException();
            }

            this.clientId = clientId;

            return this;
        }

        public Builder withClientSecret(String clientSecret) {
            if (clientSecret == null || clientSecret.isEmpty()) {
                throw new IllegalArgumentException();
            }

            this.clientSecret = clientSecret;

            return this;
        }
    }
}
