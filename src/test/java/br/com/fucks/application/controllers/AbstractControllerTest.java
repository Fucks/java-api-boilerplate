package br.com.fucks.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;


public class AbstractControllerTest {

    @Autowired
    ObjectMapper mapper;

    protected String toJson(Object body) throws JsonProcessingException {
        return mapper.writeValueAsString(body);
    }

    protected <T> T toObject(String body, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(body, clazz);
    }
}
