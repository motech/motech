package org.motechproject.osgi.web;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Header {

    private static final String HEADER_HTML = "header.html";

    public String asString() {
        InputStream is = null;
        StringWriter writer = new StringWriter();
        is = Thread.currentThread().getContextClassLoader().getResourceAsStream(HEADER_HTML);
        try {
            try {
                IOUtils.copy(is, writer);
            } catch (IOException e) {
                throw new MotechException("Header could not be written", e);
            }
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }
        return writer.toString();
    }

}
