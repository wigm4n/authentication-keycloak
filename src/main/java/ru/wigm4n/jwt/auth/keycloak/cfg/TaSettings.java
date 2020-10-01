package ru.wigm4n.jwt.auth.keycloak.cfg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class TaSettings {
    @Value("${ta_username}")
    private String username;
    @Value("${ta_password}")
    private String password;
}
