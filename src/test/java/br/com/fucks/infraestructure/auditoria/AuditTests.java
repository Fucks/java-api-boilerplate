package br.com.fucks.infraestructure.auditoria;

import br.com.fucks.domain.model.group.Group;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import br.com.fucks.infrastructure.services.DefaultCrudUseCase;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
public class AuditTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DefaultCrudUseCase<Group, Long> useCase;

    @Test
    public void shouldAddUsername__WhenAuditEntity__WithSuccess() {

        SecurityContext secContext = SecurityContextHolder.createEmptyContext();

        secContext.setAuthentication(new JwtAuthenticationToken(
                Jwt.withTokenValue("token")
                        .claim("subject", "subject@gmail.com")
                        .header("iss", "")
                        .build()
        ));

        SecurityContextHolder.setContext(secContext);

        PlatformTransactionManager txMgr = applicationContext.getBean(PlatformTransactionManager.class);

        TransactionStatus status = txMgr.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

        Group group = Group
                .builder()
                .name("Test")
                .abbreviation("TAC")
                .build();

        var entity = useCase.insert(group);

        txMgr.commit(status);

        var history = (List<Map<String, Object>>) entityManager
                .createNativeQuery("SELECT * FROM audit.revinfo")
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();

        var dbEntity = useCase.findById(entity.getId()).get();

        Assertions.assertEquals("subject@gmail.com", history.get(0).get("username"));
        Assertions.assertEquals("subject@gmail.com", dbEntity.getCreatedByUsername());
        Assertions.assertNotNull(dbEntity.getCreatedAt());
    }

    @Test
    public void shouldAddRevision__WhenInsert__WithSuccess() {

        PlatformTransactionManager txMgr = applicationContext.getBean(PlatformTransactionManager.class);

        TransactionStatus status = txMgr.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

        Group group = Group
                .builder()
                .name("Test")
                .abbreviation("TAC")
                .build();

        useCase.insert(group);

        txMgr.commit(status);

        var history = (List<Map<String, Object>>) entityManager
                .createNativeQuery("SELECT * FROM audit.group_aud")
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();

        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(0, Byte.toUnsignedInt((Byte) history.get(0).get("revtype")));
    }

    @Test
    @Sql(statements = {
            "INSERT INTO tbgroup (id, name, abbreviation, enabled, created_at, created_by) " +
                    "VALUES (100, 'Group One', 'GO', TRUE, '2022-09-29 00:00:00', 'fucks'); "
    })
    public void shouldAddRevision__WhenUpdate__WithSuccess() throws EntityNotFoundException {

        SecurityContext secContext = SecurityContextHolder.createEmptyContext();

        secContext.setAuthentication(new JwtAuthenticationToken(
                Jwt.withTokenValue("token")
                        .claim("subject", "subject@gmail.com")
                        .header("iss", "")
                        .build()
        ));

        SecurityContextHolder.setContext(secContext);

        PlatformTransactionManager txMgr = applicationContext.getBean(PlatformTransactionManager.class);

        TransactionStatus status = txMgr.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

        useCase.update(100L, Group.builder()
                .name("Group Teste")
                .abbreviation("AHA")
                .build());

        txMgr.commit(status);

        var history = (List<Map<String, Object>>) entityManager
                .createNativeQuery("SELECT * FROM audit.group_aud")
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();

        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(1, Byte.toUnsignedInt((Byte) history.get(0).get("revtype")));

        var entity = useCase.findById(100L).get();

        Assertions.assertEquals("subject@gmail.com", entity.getUpdatedByUsername());
        Assertions.assertNotNull(entity.getUpdatedAt());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO tbgroup (id, name, abbreviation, enabled, created_at, created_by) " +
                    "VALUES (200, 'Group One', 'GO', TRUE, '2022-09-29 00:00:00', 'fucks'); "
    })
    public void shouldAddRevision__WhenDelete__WithSuccess() throws EntityNotFoundException {

        SecurityContext secContext = SecurityContextHolder.createEmptyContext();
        secContext.setAuthentication(new JwtAuthenticationToken(
                Jwt.withTokenValue("token")
                        .claim("subject", "subject@gmail.com")
                        .header("iss", "")
                        .build()
        ));

        SecurityContextHolder.setContext(secContext);

        PlatformTransactionManager txMgr = applicationContext.getBean(PlatformTransactionManager.class);

        TransactionStatus status = txMgr.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

        useCase.delete(200L);

        txMgr.commit(status);

        var history = (List<Map<String, Object>>) entityManager
                .createNativeQuery("SELECT * FROM audit.group_aud")
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();

        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(2, Byte.toUnsignedInt((Byte) history.get(0).get("revtype")));

        var entity = useCase.findById(200L);

        Assertions.assertTrue(entity.isEmpty());
    }

    @Test
    public void shouldAddRevision__WhenInsert__WithoutAuthenticatedUser() {

        PlatformTransactionManager txMgr = applicationContext.getBean(PlatformTransactionManager.class);

        TransactionStatus status = txMgr.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

        Group group = Group
                .builder()
                .name("Group test")
                .abbreviation("TAC")
                .build();

        var entity = useCase.insert(group);

        txMgr.commit(status);

        var history = (List<Map<String, Object>>) entityManager
                .createNativeQuery("SELECT * FROM audit.revinfo")
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();

        var dbEntity = useCase.findById(entity.getId()).get();

        Assertions.assertEquals("System", history.get(0).get("username"));
        Assertions.assertEquals("System", dbEntity.getCreatedByUsername());
        Assertions.assertNotNull(dbEntity.getCreatedAt());
    }

}
