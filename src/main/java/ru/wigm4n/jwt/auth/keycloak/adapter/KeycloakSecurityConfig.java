package ru.wigm4n.jwt.auth.keycloak.adapter;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import ru.wigm4n.jwt.auth.keycloak.configuration.UnauthorizedEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@EnableConfigurationProperties(KeycloakConfigurationProperties.class)
@RequiredArgsConstructor
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    private final KeycloakConfigurationProperties keycloakConfigurationProperties;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    public KeycloakConfigResolver KeycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        if (keycloakConfigurationProperties.isRolesEnabled()) {
            http.authorizeRequests()
                .antMatchers("/api/v1/anonymous").permitAll()
                .antMatchers("/api/v1/user").hasAnyRole("user")
                .antMatchers("/api/v1/admin").hasAnyRole("admin")
                .antMatchers("/api/v1/all-user").hasAnyRole("user", "admin");
        }
        http.authorizeRequests()
            .anyRequest()
            .authenticated();
        http.csrf().disable();
        http.exceptionHandling().authenticationEntryPoint(new UnauthorizedEntryPoint());
    }
}
