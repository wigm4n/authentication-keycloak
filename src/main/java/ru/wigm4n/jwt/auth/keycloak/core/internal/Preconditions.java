package ru.wigm4n.jwt.auth.keycloak.core.internal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Preconditions {

    public static void checkNotNull(Object val) throws IllegalArgumentException {
        checkNotNull(val, "Argument can't be null.");
    }

    public static void checkNotNull(Object val, String msg) throws IllegalArgumentException {
        if (val == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void checkNotNullOrEmpty(String val) throws IllegalArgumentException {
        checkNotNullOrEmpty(val, "Argument can't be null or empty string.");
    }

    public static void checkNotNullOrEmpty(String val, String msg) throws IllegalArgumentException {
        if (val == null || val.isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
    }
}
