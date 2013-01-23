package org.motechproject.sms.api.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.osgi.web.Header;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class PageHeader implements Header {

    private static final String HEADER_HTML = "header.html";

    public String asString() {
        InputStream is = null;
        StringWriter writer = new StringWriter();
        is = this.getClass().getClassLoader().getResourceAsStream(HEADER_HTML);
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
