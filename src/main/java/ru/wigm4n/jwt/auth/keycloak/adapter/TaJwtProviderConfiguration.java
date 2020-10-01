package ru.wigm4n.jwt.auth.keycloak.adapter;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import ru.wigm4n.jwt.auth.keycloak.rest.RestTemplateConfiguration;
import ru.wigm4n.jwt.auth.keycloak.cfg.TaJwtProviderSettings;
import ru.wigm4n.jwt.auth.keycloak.cfg.TaSettings;
import ru.wigm4n.jwt.auth.keycloak.core.impl.DefaultTaJwtProvider;
import ru.wigm4n.jwt.auth.keycloak.core.JwtValidator;
import ru.wigm4n.jwt.auth.keycloak.core.TaJwtProvider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@EnableConfigurationProperties({TaJwtProviderConfiguration.class})
@ConfigurationProperties("jwt")
@org.springframework.context.annotation.PropertySource(name = "settings",
                                                       value = "${auth.jwt.settings_source_yaml}",
                                                       factory = TaJwtProviderConfiguration.YamlPropertySourceFactory.class)

@Data
public class TaJwtProviderConfiguration {

    private final Environment environment;
    private OpenIdConnectEndpoint openIdConnectEndpoint;

    @Bean
    @ConditionalOnMissingBean
    public TaJwtProvider defaultTaJwtProvider(JwtValidator jwtValidator,
                                              RestTemplateConfiguration restTemplateConfiguration,
                                              TaSettings taSettings) {
        return new DefaultTaJwtProvider(get(), jwtValidator, restTemplateConfiguration, taSettings);
    }

    private TaJwtProviderSettings get() {
        Optional.ofNullable(openIdConnectEndpoint)
                .orElseThrow(() -> {
                    throw new IllegalStateException("OpenIdConnectEndpoint should be defined");
                });

        return buildTaJwtProviderCfg(openIdConnectEndpoint);
    }

    private TaJwtProviderSettings buildTaJwtProviderCfg(OpenIdConnectEndpoint cfg) {
        return TaJwtProviderSettings.builder()
                                    .setURI(cfg.getLocation())
                                    .setClientId(environment.getProperty(cfg.getClientIdEnv()))
                                    .setClientSecret(environment.getProperty(cfg.getClientSecretEnv()))
                                    .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpenIdConnectEndpoint {
        private String id;
        private String location;
        private String clientIdEnv;
        private String clientSecretEnv;
    }

    static class YamlPropertySourceFactory implements PropertySourceFactory {

        @Override
        public org.springframework.core.env.PropertySource<?> createPropertySource(
            String name, EncodedResource resource) throws IOException {

            if (!resource.getResource().isReadable()) {
                throw new FileNotFoundException();
            }

            YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
            List<PropertySource<?>> propertySources = loader.load(name, resource.getResource());
            return propertySources.stream().findFirst().orElse(null);
        }
    }
}
