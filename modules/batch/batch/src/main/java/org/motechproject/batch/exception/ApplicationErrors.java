package org.motechproject.batch.exception;

import org.motechproject.batch.util.BatchConstants;
import org.springframework.http.HttpStatus;

public enum ApplicationErrors implements BatchErrors {

    BAD_REQUEST(1001, "One or more input parameter(s) may be wrong",
            HttpStatus.BAD_REQUEST), JOB_NOT_FOUND(1002, "Job not found",
            HttpStatus.BAD_REQUEST), DUPLICATE_JOB(1003, "Duplicate Job",
            HttpStatus.BAD_REQUEST), DATABASE_OPERATION_FAILED(3003,
            "Error in querying database", HttpStatus.INTERNAL_SERVER_ERROR), FILE_READING_WRTING_FAILED(
            3002, "Error while reading from or writing to file",
            HttpStatus.INTERNAL_SERVER_ERROR), JOB_TRIGGER_FAILED(3001,
            "Error in starting job", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private String message;
    private HttpStatus httpStatus;

    private ApplicationErrors() {
        code = BatchConstants.CODE_LENGTH;
    }

    private ApplicationErrors(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
