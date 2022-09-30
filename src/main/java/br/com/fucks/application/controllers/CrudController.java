package br.com.fucks.application.controllers;

import br.com.fucks.application.parseradapters.IParserAdapter;
import br.com.fucks.domain.model.UpdatableEntity;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import br.com.fucks.infrastructure.services.DefaultCrudUseCase;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Log4j2
public class CrudController<MODEL, ENTITY extends UpdatableEntity<ENTITY>, ID> {

    public final String WRITE_ROLE;
    public final String READ_ROLE;

    @Autowired
    protected DefaultCrudUseCase<ENTITY, ID> service;

    @Autowired
    protected IParserAdapter<MODEL, ENTITY> parserAdapter;

    public CrudController(String writeRole, String readRole) {
        WRITE_ROLE = writeRole;
        READ_ROLE = readRole;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#this.this.WRITE_ROLE, #this.this.READ_ROLE)")
    public ResponseEntity<MODEL> findById(@PathVariable ID id) {

        log.info("Performing 'findById' with id: {}", id);

        var hasEntity = this.service.findById(id);

        if (hasEntity.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity
                .ok(parserAdapter.toModel(hasEntity.get()));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasPermission(#this.this.WRITE_ROLE)")
    public ResponseEntity<MODEL> insert(@RequestBody MODEL body) {
        log.info("Performing 'insert'");

        var response = this.service.insert(this.parserAdapter.toEntity(body));

        return ResponseEntity
                .accepted()
                .body(this.parserAdapter.toModel(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#this.this.WRITE_ROLE)")
    public ResponseEntity<MODEL> update(@PathVariable ID id, @RequestBody MODEL body) throws EntityNotFoundException {
        log.info("Performing 'update'");

        var response = this.service.update(id, this.parserAdapter.toEntity(body));

        return ResponseEntity
                .ok(this.parserAdapter.toModel(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#this.this.WRITE_ROLE)")
    public ResponseEntity<Object> delete(@PathVariable ID id) throws EntityNotFoundException {
        log.info("Performing 'delete'");

        this.service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/page")
    @PageableAsQueryParam
    @PreAuthorize("hasPermission(#this.this.WRITE_ROLE, #this.this.READ_ROLE)")
    public ResponseEntity<Page<MODEL>> listAll(@PageableDefault Pageable page) {
        log.info("Performing 'listAll' with page: {}", page);

        var response = this.service.listAll(page).map(this.parserAdapter::toModel);

        return ResponseEntity
                .ok(response);
    }
}
