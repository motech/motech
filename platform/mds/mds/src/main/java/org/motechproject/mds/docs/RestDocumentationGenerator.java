package org.motechproject.mds.docs;

import java.io.Writer;
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
     * @param serverPrefix  prefix of the server url
     * @param locale  the locale to be used while generating REST documentation
     */
    void generateDocumentation(Writer writer, String serverPrefix, Locale locale);
}
