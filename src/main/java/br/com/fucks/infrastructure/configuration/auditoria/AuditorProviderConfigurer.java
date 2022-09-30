package br.com.fucks.infrastructure.configuration.auditoria;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Log4j2
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "zonedDateTimeProvider")
public class AuditorProviderConfigurer {

    @Bean
    public AuditorAware<String> auditorProvider() {

        return () -> {

            var authentication = Optional
                    .ofNullable(SecurityContextHolder.getContext().getAuthentication());

            if (authentication.isEmpty()) {
                return Optional.empty();
            }

            try {
                var name = (String) ((Jwt) authentication.get().getPrincipal()).getClaim("subject");
                return Optional.of(name);
            } catch (Exception e) {
                log.error(e);
                return Optional.empty();
            }
        };
    }
}
