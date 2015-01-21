package org.motechproject.mds.web.rest.docs;

import java.io.Writer;
import java.util.List;

/**
 * Interface for generating REST API documentation from the entity model.
 * Since the REST API itself is dynamic, we generate the descriptors for it
 * based on the schema.
 */
public interface RestDocumentationGenerator {

    void generateDocumentation(Writer writer, List<RestEntry> restEntries);
}
