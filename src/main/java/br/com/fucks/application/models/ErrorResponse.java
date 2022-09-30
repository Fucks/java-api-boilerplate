package br.com.fucks.application.models;

import lombok.Data;

@Data
public class ErrorResponse {

    private String message;

    private String stackTrace;

    public ErrorResponse(String message, String stackTrace) {
        this.message = message;
        this.stackTrace = stackTrace;
    }
}
