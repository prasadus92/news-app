package com.upday.news.controller.errors.dto;

import java.io.Serializable;

public class FieldErrorDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String objectName;

    private String field;

    private String message;

    public FieldErrorDto(String dto, String field, String message) {
        this.objectName = dto;
        this.field = field;
        this.message = message;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
