package org.motechproject.mds.service;

import java.io.Writer;

/**
 * This service allows retrieval of dynamically generated MDS REST documentation.
 * This is an OSGi service interface, it is used by the mds-web module to serve the documentation through HTTP.
 *
 * The documentation returned is a JSON representation of the API in Swagger json
 * format.
 */
public interface RestDocumentationService {

    /**
     * Writes REST API documentation the documentation to the writer provided.
     * @param writer the output for the documentation.
     * @param serverPrefix the prefix of the server, for example /motech-platform-server, will be used in the swagger
     *                     spec
     */
    void retrieveDocumentation(Writer writer, String serverPrefix);
}
