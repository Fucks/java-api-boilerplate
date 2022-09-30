package br.com.fucks.application.parseradapters;

public interface IParserAdapter<MODEL, ENTITY> {

    MODEL toModel(ENTITY entity);
    ENTITY toEntity(MODEL model);

}
