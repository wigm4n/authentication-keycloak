package ru.wigm4n.jwt.auth.keycloak.core.impl;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import ru.wigm4n.jwt.auth.keycloak.core.internal.Preconditions;
import ru.wigm4n.jwt.auth.keycloak.core.Jwt;

import java.util.Optional;

import static ru.wigm4n.jwt.auth.keycloak.core.internal.Preconditions.checkNotNull;
import static ru.wigm4n.jwt.auth.keycloak.core.internal.Preconditions.checkNotNullOrEmpty;


public class JwtImpl implements Jwt {
    private final DecodedJWT impl;

    public JwtImpl(DecodedJWT impl) {
        Preconditions.checkNotNull(impl);
        this.impl = impl;
    }

    @Override
    public boolean isClaimPresent(String name) {
        return this.impl.getClaims().containsKey(name);
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getClaimAs(String name, Class<T> clazz) {
        Preconditions.checkNotNullOrEmpty(name);
        Preconditions.checkNotNull(clazz);

        Claim claim = impl.getClaim(name);

        if (claim.isNull()) {
            return Optional.empty();
        }

        T result;

        if (clazz == String.class) {
            result = (T) claim.asString();
        } else {
            result = claim.as(clazz);
        }

        return Optional.ofNullable(result);
    }

    public DecodedJWT get() {
        return this.impl;
    }

    @Override
    public String toString() {
        return this.impl.getToken();
    }
}
