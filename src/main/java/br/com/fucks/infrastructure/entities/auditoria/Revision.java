package br.com.fucks.infrastructure.entities.auditoria;

import br.com.fucks.infrastructure.configuration.auditoria.RevisionListener;
import lombok.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@RevisionEntity(RevisionListener.class)
public class Revision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long revision;

    @RevisionTimestamp
    @Column(nullable = false)
    private Date timestamp;

    @Column(nullable = false)
    private String username;
}
