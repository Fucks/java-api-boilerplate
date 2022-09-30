package br.com.fucks.application.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Profile("!test")
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                    .antMatchers("/actuator/health").permitAll()
                    .anyRequest()
                        .authenticated()
                .and()
                .oauth2ResourceServer().jwt()
                .and().and()
                .cors();

        return http.build();
    }

}
