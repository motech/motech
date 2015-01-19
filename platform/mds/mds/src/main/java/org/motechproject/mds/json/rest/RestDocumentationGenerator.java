package org.motechproject.mds.json.rest;

import java.io.PrintWriter;
import java.util.List;

/**
 * Interface for generating REST API documentation from the entity model.
 * Since the REST API itself is dynamic, we generate the descriptors for it
 * based on the schema.
 */
public interface RestDocumentationGenerator {

    void generateDocumentation(PrintWriter writer, List<RestEntry> restEntries);
}
