package ru.wigm4n.jwt.auth.keycloak.exception;

public class GetJwtException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GetJwtException(String message) {
        super(message);
    }

    public GetJwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
