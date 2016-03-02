package org.motechproject.mds.web.controller;

import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.exception.MdsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * The <code>MdsController</code> is a basic controller for other controllers defined in
 * the mds module. Its function is to handle all {@link org.motechproject.mds.exception.MdsException}
 * exceptions from extended classes.
 *
 * @see MdsException
 */
public abstract class MdsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsController.class);

    @ExceptionHandler(MdsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public String handleMdsException(final MdsException exception) throws IOException {
        LOGGER.error("Error: " + exception.getMessage(), exception);

        if (exception.getMessageKey() == null) {
            return "error";
        } else if (exception.getParams() == null) {
            return String.format("key:%s", exception.getMessageKey());
        } else {
            return String.format("key:%s\nparams:%s", exception.getMessageKey(), exception.getParams());
        }
    }

    protected TypeDto textAreaUIType() {
        TypeDto textAreaType = new TypeDto();

        textAreaType.setDefaultName("textArea");
        textAreaType.setDisplayName("mds.field.textArea");
        textAreaType.setDescription("mds.field.description.textArea");
        textAreaType.setTypeClass("textArea");

        return textAreaType;
    }
}
