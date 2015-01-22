package org.motechproject.mds.docs;

import org.motechproject.mds.domain.EntityInfo;

import java.io.Writer;
import java.util.List;

/**
 * Interface for generating REST API documentation from the entity model.
 * Since the REST API itself is dynamic, we generate the descriptors for it
 * based on the schema.
 */
public interface RestDocumentationGenerator {

    void generateDocumentation(Writer writer, List<EntityInfo> restEntries);
}
