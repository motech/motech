package org.motechproject.server.jndi;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Implementation of the {@link org.motechproject.server.jndi.JndiLookupService}.
 * Switches the context ClassLoader to the server one in order to read JNDI resources.
 */
public class JndiLookupServiceImpl implements JndiLookupService {

    @Override
    public void writeToFile(String url, String destinationFile) throws IOException {
        URL urlObj = new URL(url);
        File file = new File(destinationFile);

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try (InputStream in = urlObj.openStream()) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(in, fos);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }
    }
}
