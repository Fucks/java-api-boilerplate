package br.com.fucks.infrastructure.services;

import br.com.fucks.domain.model.UpdatableEntity;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public class DefaultCrudUseCase<T extends UpdatableEntity<T>, ID> {

    @Autowired
    protected JpaRepository<T, ID> repository;

    public T insert(T entity) {
        return this.repository.saveAndFlush(entity);
    }

    public T update(ID id, T entity) throws EntityNotFoundException {

        var hasEntity = this.findById(id);

        if (hasEntity.isEmpty()) {
            throw new EntityNotFoundException();
        }

        var dbEntity = hasEntity.get();
        dbEntity.update(entity);

        return this.repository.saveAndFlush(dbEntity);
    }

    public void delete(ID id) throws EntityNotFoundException {

        var hasEntity = this.findById(id);

        if (hasEntity.isEmpty()) {
            throw new EntityNotFoundException();
        }

        this.repository.deleteById(id);
    }

    public Page<T> listAll(Pageable page) {
        return this.repository.findAll(page);
    }

    public Optional<T> findById(ID id) {
        return this.repository.findById(id);
    }
}
