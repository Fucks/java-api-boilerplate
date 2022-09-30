package br.com.fucks.application.controllers;

import br.com.fucks.application.models.group.GroupModel;
import br.com.fucks.application.parseradapters.IParserAdapter;
import br.com.fucks.domain.model.group.Group;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import br.com.fucks.infrastructure.services.DefaultCrudUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;

@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
class CrudControllerTests {

    private JpaRepository<Group, Long> repository;

    private CrudController<GroupModel, Group, Long> crudController;

    public CrudControllerTests(
            @Autowired JpaRepository<Group, Long> repository,
            @Autowired IParserAdapter<GroupModel, Group> parserAdapter,
            @Autowired DefaultCrudUseCase<Group, Long> service) {

        crudController = new CrudController("", "");

        this.repository = repository;
        crudController.service = service;
        crudController.parserAdapter = parserAdapter;
    }

    @Test
    @Sql(statements = {
            "INSERT INTO tbgroup (id, name, abbreviation, enabled) " +
                    "VALUES (100, 'Group One', 'GO', TRUE); "
    })
    void shouldReturn200__WhenFindById__WithSuccess() {
        var response = crudController.findById(100L);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturn404__WhenFindById__WithEntityNotFound() {
        var response = crudController.findById(1L);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldReturn201__WhenInsert__WithError() {

        var body = GroupModel.builder()
                .name("Entity")
                .abbreviation("SG")
                .build();

        try {
            var response = crudController.insert(body);
            Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            Assertions.assertNotNull(response.getBody());
            Assertions.assertNotNull(response.getBody().getId());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldThrowsException__WhenInsert__WithError() {

        var body = GroupModel
                .builder()
                .build();

        try {
            var response = crudController.insert(body);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals(DataIntegrityViolationException.class, e.getClass());
        }
    }

    @Test
    @Sql(statements = {
            "INSERT INTO tbgroup (id, name, abbreviation, enabled) " +
                    "VALUES (20, 'Group One', 'GO', TRUE); "
    })
    void shouldReturn200__WhenUpdate__WithSuccess() {
        var body = GroupModel
                .builder()
                .id(20L)
                .name("New Name")
                .abbreviation("NS")
                .build();

        Assertions.assertEquals("Group One", this.repository.findById(20L).get().getName());

        try {
            var response = crudController.update(20L, body);

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertEquals("New Name", repository.findById(20L).get().getName());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldThrowsException__WhenUpdate__WithEntityNotFound() {

        var body = GroupModel
                .builder()
                .id(30L)
                .name("New Name")
                .abbreviation("NS")
                .build();

        Assertions.assertTrue(this.repository.findById(30L).isEmpty());

        try {
            crudController.update(30L, body);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    @Sql(statements = {
            "INSERT INTO tbgroup (id, name, abbreviation, enabled) " +
                    "VALUES (23, 'Group One', 'GO', TRUE); "
    })
    void shouldReturn200__WhenDelete__WithSuccess() {

        try {
            var response = crudController.delete(23L);

            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            Assertions.assertTrue(repository.findById(23L).isEmpty());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    void shouldThrowsException__WhenDelete__WithEntityNotFound() {

        try {
            var response = crudController.delete(23L);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals(EntityNotFoundException.class, e.getClass());
        }
    }

    @Test
    void shouldReturn200__WhenListAll__WithSuccess() {
        try {
            var todos = crudController.listAll(Pageable.unpaged());
            Assertions.assertEquals(HttpStatus.OK, todos.getStatusCode());
            Assertions.assertTrue(todos.getBody().getTotalElements() > 0);
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
