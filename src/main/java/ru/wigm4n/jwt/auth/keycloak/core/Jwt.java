package ru.wigm4n.jwt.auth.keycloak.core;

import java.util.Optional;

public interface Jwt {
    boolean isClaimPresent(String var1);

    <T> Optional<T> getClaimAs(String name, Class<T> clazz);

    default Optional<String> getClaimAsString(String name) {
        return getClaimAs(name, String.class);
    }
}
