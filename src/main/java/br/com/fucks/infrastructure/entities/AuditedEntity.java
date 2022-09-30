package br.com.fucks.infrastructure.entities;


import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
@Log4j2
public abstract class AuditedEntity implements Serializable {

    @CreatedBy
    @Column(nullable = false)
    protected String createdByUsername;

    @CreatedDate
    @Column(nullable = false)
    protected ZonedDateTime createdAt;

    @LastModifiedBy
    @Column
    protected String updatedByUsername;

    @LastModifiedDate
    @Column
    protected ZonedDateTime updatedAt;
}
