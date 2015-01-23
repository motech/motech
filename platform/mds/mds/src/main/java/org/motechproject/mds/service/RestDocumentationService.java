package org.motechproject.mds.service;

import java.io.Writer;

/**
 * This service allows retrieval of dynamically generated MDS REST documentation.
 * The documentation is internally retrieved from {@link org.motechproject.mds.repository.RestDocsRepository}.
 * This is an OSGi service interface, it is used used by the mds-web module to server
 * the documentation to the UI.
 *
 * The documentation returned is a JSON representation of the API in Swagger json
 * format.
 */
public interface RestDocumentationService {

    /**
     * Writes REST API documentation the documentation to the writer provided.
     * @param writer The output for the documentation.
     */
    void retrieveDocumentation(Writer writer);
}
