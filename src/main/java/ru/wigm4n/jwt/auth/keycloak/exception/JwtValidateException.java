package ru.wigm4n.jwt.auth.keycloak.exception;

/**
 * Ошибка валидации Jwt token. Базовый класс для всех исключений механизма валидации токена
 * <p>
 * Это может быть ошибка разбора токена, недостаточно сведений, недоступность сведений, ошибка валидации подписи и т.п.
 */
public class JwtValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JwtValidateException(String msg) {
        super(msg);
    }

    public JwtValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}
