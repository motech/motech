package org.motechproject.mds.docs;

import org.motechproject.mds.domain.Entity;

import java.io.Writer;
import java.util.List;
import java.util.Locale;

/**
 * Interface for generating REST API documentation from the entity model.
 * Since the REST API itself is dynamic, we generate the descriptor for it
 * based on the schema.
 */
public interface RestDocumentationGenerator {

    /**
     * Generated the spec file for MDS REST API and writes it to the provided output.
     *
     * @param writer  the output to which the spec file will be written
     * @param restEntries  entities that will be used for generation of the API, they will be filtered based on their
     *                     REST properties
     * @param serverPrefix  prefix of the server url
     * @param locale  the locale to be used while generating REST documentation
     */
    void generateDocumentation(Writer writer, List<Entity> restEntries, String serverPrefix, Locale locale);
}
