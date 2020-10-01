package ru.wigm4n.jwt.auth.keycloak.adapter;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "custom.keycloak")
@Data
@NoArgsConstructor
public class KeycloakConfigurationProperties {
    private boolean rolesEnabled;
}
