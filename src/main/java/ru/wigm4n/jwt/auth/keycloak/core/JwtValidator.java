package ru.wigm4n.jwt.auth.keycloak.core;

public interface JwtValidator {

    Jwt decode(String token);

    Jwt validate(Jwt token);
}
