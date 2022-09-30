package br.com.fucks.application.controllers.v1;

import br.com.fucks.application.controllers.CrudController;
import br.com.fucks.application.decorators.filter.LikeFilterDecorator;
import br.com.fucks.application.models.group.GroupModel;
import br.com.fucks.domain.model.group.Group;
import br.com.fucks.domain.model.group.GroupPermissions;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import br.com.fucks.infrastructure.services.group.GroupCrudUseCase;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/group")
public class GroupController extends CrudController<GroupModel, Group, Long> {

    @Autowired
    private GroupCrudUseCase useCase;

    public GroupController() {
        super(GroupPermissions.WRITE, GroupPermissions.READ);
    }

    @PostMapping("/disable/{id}")
    @PreAuthorize("hasPermission(#this.this.WRITE_ROLE)")
    public ResponseEntity<Object> disable(@PathVariable Long id) throws EntityNotFoundException {

        this.useCase.disable(id);

        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("/list")
    @PageableAsQueryParam
    @PreAuthorize("hasPermission(#this.this.READ_ROLE, #this.this.WRITE_ROLE)")
    public ResponseEntity<Page<GroupModel>> listByFilter(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "false") Boolean showDisabled,
            @PageableDefault Pageable page) {

        var response = this.useCase
                .listByFilter(new LikeFilterDecorator(filter).decorate(), showDisabled, page)
                .map(this.parserAdapter::toModel);

        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#this.this.WRITE_ROLE)")
    public ResponseEntity<Object> delete(@PathVariable Long id) throws EntityNotFoundException {

        this.useCase.delete(id);

        return ResponseEntity
                .ok()
                .build();
    }
}
