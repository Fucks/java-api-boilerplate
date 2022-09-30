package br.com.fucks.domain.model.group;

import br.com.fucks.domain.model.UpdatableEntity;
import br.com.fucks.infrastructure.entities.AuditedEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Entity
@Builder
@Audited
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "tbgroup")
public class Group extends AuditedEntity implements UpdatableEntity<Group> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String abbreviation;

    @Column(nullable = false)
    private Boolean enabled;

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    @Override
    public void update(Group fromValues) {
        this.name = fromValues.getName();
        this.abbreviation = fromValues.getAbbreviation();
    }
}
