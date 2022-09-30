package br.com.fucks.infraestructure.services;

import br.com.fucks.domain.model.group.Group;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import br.com.fucks.infrastructure.services.DefaultCrudUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZonedDateTime;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DefaultCrudUseCaseTests {

    @Mock
    private JpaRepository<Group, Long> repository;

    @InjectMocks
    private DefaultCrudUseCase<Group, Long> crudUseCase;

    @Test
    void shouldInsert__WhenInsertNewEntity__WithSuccess() {

        var entity = Group.builder()
                .name("Teste")
                .abbreviation("TT")
                .build();

        try {
            this.crudUseCase.insert(entity);
        } catch (Exception e) {
            Assertions.fail();
        }

        Mockito.verify(this.repository, Mockito.atLeastOnce()).saveAndFlush(entity);
    }

    @Test
    void shouldUpdate__WhenUpdateExistingEntity__WithSuccess() {

        var entity = Group.builder()
                .name("Teste")
                .abbreviation("TT")
                .build();

        Mockito.when(this.repository.findById(1L))
                .thenReturn(Optional.of(entity));

        try {
            this.crudUseCase.update(1L, entity);
        } catch (Exception e) {
            Assertions.fail();
        }

        Mockito.verify(this.repository, Mockito.atLeastOnce()).saveAndFlush(entity);
    }

    @Test
    void shouldThrowsException__WhenUpdateEntityNotFound__WithError() {

        var entity = Group.builder()
                .name("Teste")
                .abbreviation("TT")
                .build();

        Mockito.when(this.repository.findById(1L))
                .thenReturn(Optional.empty());

        try {
            this.crudUseCase.update(1L, entity);
            Assertions.fail();
        } catch (EntityNotFoundException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    void shouldDelete__WhenDeleteExistingEntity__WithSuccess() {

        var entity = Group.builder()
                .name("Teste")
                .abbreviation("TT")
                .build();

        Mockito.when(this.repository.findById(1L))
                .thenReturn(Optional.of(entity));

        try {
            this.crudUseCase.delete(1L);
        } catch (Exception e) {
            Assertions.fail();
        }

        Mockito.verify(this.repository, Mockito.atLeastOnce()).deleteById(1L);
    }

    @Test
    void shouldThrowsException__WhenDeleteEntityNotFound__WithError() {

        Mockito.when(this.repository.findById(1L))
                .thenReturn(Optional.empty());

        try {
            this.crudUseCase.delete(1L);
            Assertions.fail();
        } catch (EntityNotFoundException e) {
            Assertions.assertTrue(true);
        }

        Mockito.verify(this.repository, Mockito.never()).deleteById(1L);
    }
}
