package ru.wigm4n.jwt.auth.keycloak.core.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.wigm4n.jwt.auth.keycloak.core.internal.Preconditions;
import ru.wigm4n.jwt.auth.keycloak.exception.JwtValidateException;
import ru.wigm4n.jwt.auth.keycloak.core.Jwt;
import ru.wigm4n.jwt.auth.keycloak.core.JwtValidator;

import static ru.wigm4n.jwt.auth.keycloak.core.internal.Preconditions.checkNotNull;

@NoArgsConstructor
@Service
public final class JwtValidatorImpl implements JwtValidator {

    private final JWT jwtDecoder = new JWT();

    @Override
    public Jwt decode(String token) {
        Preconditions.checkNotNull(token, "Token should be defined and not empty");
        try {
            return new JwtImpl(jwtDecoder.decodeJwt(token));
        } catch (JWTDecodeException e) {
            throw new JwtValidateException("Failed to decode defined token.", e);
        }
    }

    // должен выполнять валидацию пришедшего токена на kid идентификатор ключа, iss издатель (uri), alg алгоритм подписи
    @Override
    public Jwt validate(Jwt token) {
        //TODO: token validation
        return token;
    }
}
