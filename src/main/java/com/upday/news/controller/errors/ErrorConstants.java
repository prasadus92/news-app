package com.upday.news.controller.errors;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "ConcurrencyFailureError";

    public static final String ERR_VALIDATION = "ValidationError";

    public static final String ERR_METHOD_NOT_SUPPORTED = "MethodNotSupportedError";

    public static final String ERR_INTERNAL_SERVER_ERROR = "InternalServerError";

    public static final String ERR_CONSTRAINT_VIOLATION = "AlreadyExists";

    public static final String ERR_PERSISTENT_OBJECT_EXCEPTION = "DetachedEntityPassed";

    private ErrorConstants() {
    }
}
