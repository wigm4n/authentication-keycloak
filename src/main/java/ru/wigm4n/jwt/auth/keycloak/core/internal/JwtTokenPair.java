package ru.wigm4n.jwt.auth.keycloak.core.internal;

import lombok.Data;
import ru.wigm4n.jwt.auth.keycloak.core.Jwt;

@Data
public class JwtTokenPair {
    private final Jwt accessToken;
    private final Jwt refreshToken;
}
