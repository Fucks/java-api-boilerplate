package br.com.fucks.domain.model;

public interface UpdatableEntity<T> {
    void update(T fromValues);
}
