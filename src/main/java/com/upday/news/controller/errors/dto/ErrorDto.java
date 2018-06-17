package com.upday.news.controller.errors.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ErrorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;

    private String description;

    private List<FieldErrorDto> fieldErrorDtos;

    public ErrorDto(String message) {
        this(message, null);
    }

    public ErrorDto(String message, String description) {
        this.message = message;
        this.description = description;
    }

    public ErrorDto(String message, String description, List<FieldErrorDto> fieldErrorDtos) {
        this.message = message;
        this.description = description;
        this.fieldErrorDtos = fieldErrorDtos;
    }

    public void add(String objectName, String field, String message) {
        if(fieldErrorDtos == null){
            fieldErrorDtos = new ArrayList<>();
        }
        fieldErrorDtos.add(new FieldErrorDto(objectName, field, message));
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public List<FieldErrorDto> getFieldErrorDtos() {
        return fieldErrorDtos;
    }
}
