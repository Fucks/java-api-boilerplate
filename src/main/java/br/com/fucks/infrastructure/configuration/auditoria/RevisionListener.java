package br.com.fucks.infrastructure.configuration.auditoria;


import br.com.fucks.infrastructure.entities.auditoria.Revision;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.Optional;

@Component
@Log4j2
public class RevisionListener implements org.hibernate.envers.RevisionListener {

    @PrePersist
    @PreUpdate
    @PreRemove
    @Override
    public void newRevision(Object revisionEntity) {

        final Revision revision = (Revision) revisionEntity;

        var authenticated = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        var name = "System";

        if (authenticated.isPresent()) {
            try {
                name = ((Jwt) authenticated.get().getPrincipal()).getClaim("subject");
            } catch (Exception e) {
                log.error(e);
            }
        }

        revision.setUsername(name);
    }
}

