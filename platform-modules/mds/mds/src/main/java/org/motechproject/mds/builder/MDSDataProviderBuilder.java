package org.motechproject.mds.builder;

import org.motechproject.mds.dto.SchemaHolder;

/**
 * The <code>MDSDataProviderBuilder</code> class is responsible for building the
 * MDS Data Provider JSON, required to register a data provider in the Tasks module.
 */
public interface MDSDataProviderBuilder {

    /**
     * Generates JSON String, containing information about all entities with lookups.
     * It contains all the information required by the task module to register a
     * data provider.
     *
     * @param schemaHolder the current MDS schema
     * @return   JSON-formatted String, containing information about MDS Data Provider
     */
    String generateDataProvider(SchemaHolder schemaHolder);

}
