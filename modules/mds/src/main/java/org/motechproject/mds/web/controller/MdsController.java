package org.motechproject.mds.web.controller;

import org.motechproject.mds.ex.MdsException;
import org.motechproject.mds.web.ExampleData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * The <code>MdsController</code> is a basic controller for other controllers defined in
 * the mds module. Its function is to handle all {@link MdsException} exceptions from extended
 * classes.
 *
 * @see MdsException
 */
public abstract class MdsController {
    private static ExampleData exampleData = new ExampleData();

    static void setExampleData(ExampleData exampleData) {
        MdsController.exampleData = exampleData;
    }

    @ExceptionHandler(MdsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public String handleMdsException(final MdsException exception) throws IOException {
        return String.format("key:%s", exception.getMessageKey());
    }

    protected static ExampleData getExampleData() {
        return exampleData;
    }
}
