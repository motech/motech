package org.motechproject.batch.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("serial")
public class BatchException extends Exception {

    private BatchErrors batchErrors;
    private String reason;

    public BatchException(BatchErrors batchErrors) {
        super(batchErrors.getMessage());
        this.batchErrors = batchErrors;
    }

    public BatchException(BatchErrors batchErrors, String reason) {
        super(batchErrors.getMessage());
        this.batchErrors = batchErrors;
        this.reason = reason;
    }

    public BatchException(BatchErrors batchErrors, Throwable throwable) {
        super(batchErrors.getMessage(), throwable);
        this.batchErrors = batchErrors;
    }

    public BatchException(BatchErrors batchErrors, Throwable throwable,
            String reason) {
        super(batchErrors.getMessage(), throwable);
        this.batchErrors = batchErrors;
        this.reason = reason;
    }

    public int getErrorCode() {
        return batchErrors.getCode();
    }

    public String getErrorMessage() {
        if (reason == null || reason.length() < 1) {
            return this.getMessage();
        } else {
            return this.getMessage() + ". Reason: " + reason;
        }
    }

    public BatchErrors getError() {
        return batchErrors;
    }

    public String getErrorMessageDetails() {

        return getStackTraceString();
    }

    private String getStackTraceString() {
        String stackTrace = null;

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        this.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        stackTrace = stringWriter.toString();

        return stackTrace;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
