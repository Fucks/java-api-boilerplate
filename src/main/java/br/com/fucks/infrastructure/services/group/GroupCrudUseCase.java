package br.com.fucks.infrastructure.services.group;

import br.com.fucks.domain.model.group.Group;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import br.com.fucks.infrastructure.repositories.group.IGroupRepository;
import br.com.fucks.infrastructure.services.DefaultCrudUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class GroupCrudUseCase extends DefaultCrudUseCase<Group, Long> {

    @Autowired
    private IGroupRepository groupRepository;

    @Override
    public Group insert(Group entity) {
        entity.enable();
        return this.repository.saveAndFlush(entity);
    }

    public Group disable(Long id) throws EntityNotFoundException {

        var hasEntity = this.repository.findById(id);

        if (hasEntity.isEmpty()) {
            throw new EntityNotFoundException();
        }

        var dbEntity = hasEntity.get();
        dbEntity.disable();

        this.repository.saveAndFlush(dbEntity);

        return dbEntity;
    }

    public Page<Group> listByFilter(String filter, Boolean showDisabled, Pageable page) {
        return this.groupRepository.findAllByFilters(filter, showDisabled, page);
    }
}
