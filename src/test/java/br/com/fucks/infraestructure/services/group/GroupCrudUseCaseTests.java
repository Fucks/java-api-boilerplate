package br.com.fucks.infraestructure.services.group;

import br.com.fucks.domain.model.group.Group;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import br.com.fucks.infrastructure.services.group.GroupCrudUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GroupCrudUseCaseTests {

    @Autowired
    private GroupCrudUseCase crudUseCase;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSetEnabled__WhenInsert__WithSuccess() {

        var group = Group
                .builder()
                .name("Group")
                .abbreviation("AA")
                .build();

        var dbGroup = crudUseCase.insert(group);

        Assertions.assertTrue(dbGroup.getEnabled());
        Assertions.assertNotNull(dbGroup.getId());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO tbgroup (name, abbreviation, enabled) " +
                    "VALUES ('Group One', 'GO', FALSE); ",
            "INSERT INTO tbgroup (name, abbreviation, enabled) " +
                    "VALUES ('Group TWO', 'GO', TRUE); "
    })
    void shouldNotReturnDisabledEntities__WhenListByFilters__WithShowDisabledFalse() {

        var response = this.crudUseCase.listByFilter("%%", false, Pageable.unpaged());
        Assertions.assertEquals(1L, response.getTotalElements());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO tbgroup (name, abbreviation, enabled) " +
                    "VALUES ('Group One', 'GO', FALSE); ",
            "INSERT INTO tbgroup (name, abbreviation, enabled) " +
                    "VALUES ('Group TWO', 'GO', TRUE); "
    })
    void shouldReturnDisabledEntities__WhenListByFilters__WithShowDisabledTrue() {

        var response = this.crudUseCase.listByFilter("%%", true, Pageable.unpaged());
        Assertions.assertEquals(2L, response.getTotalElements());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO tbgroup (id, name, abbreviation, enabled) " +
                    "VALUES (300, 'Group One', 'GO', FALSE); ",
    })
    void shouldDisableEntity__WhenDisable__WithSuccess() {

        try {
            var response = this.crudUseCase.disable(300L);
            var dbEntity = this.crudUseCase.findById(300L).get();

            Assertions.assertFalse(response.getEnabled());
            Assertions.assertFalse(dbEntity.getEnabled());
        } catch (Exception e) {
            Assertions.fail();
        }

    }

    @Test
    void shouldThrowsException__WhenDisable__WithEntityNotFound() {

        try {
            this.crudUseCase.disable(1L);
            Assertions.fail();
        } catch (EntityNotFoundException e) {
            Assertions.assertTrue(true);
        }
    }
}
