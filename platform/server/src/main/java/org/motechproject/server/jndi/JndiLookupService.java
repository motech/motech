package org.motechproject.server.jndi;

import java.io.IOException;

/**
 * This service allows to copy jndi resource into temporary files, making
 * them accessible to libraries such as Reflections.
 */
public interface JndiLookupService {

    void writeToFile(String url, String destinationFile) throws IOException;
}
