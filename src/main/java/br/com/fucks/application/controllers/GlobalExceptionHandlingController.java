package br.com.fucks.application.controllers;

import br.com.fucks.application.models.ErrorResponse;
import br.com.fucks.infrastructure.exceptions.EntityNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandlingController {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> conflict(DataIntegrityViolationException e) {

        final String DEFAULT_ERROR_MESSAGE = "Ocorreu um erro ao realizar a operação. Verifique as informações e tente novamente.";
        final String stackTrace = ExceptionUtils.getStackTrace(e);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(DEFAULT_ERROR_MESSAGE, stackTrace));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> entityNotFound(EntityNotFoundException e) {

        final String DEFAULT_ERROR_MESSAGE = "Impossível realizar esta ação. Registro não encontrado em nossa base de dados.";

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(DEFAULT_ERROR_MESSAGE, ""));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accessDenied(AccessDeniedException e) {
        final String DEFAULT_ERROR_MESSAGE = "Acesso negado.";

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(DEFAULT_ERROR_MESSAGE, ""));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> defaultErrors(Exception e) {

        final String DEFAULT_ERROR_MESSAGE = "Ocorreu um erro. Consulte o administrador do sistema.";
        final String stackTrace = ExceptionUtils.getStackTrace(e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(DEFAULT_ERROR_MESSAGE, stackTrace));
    }

}