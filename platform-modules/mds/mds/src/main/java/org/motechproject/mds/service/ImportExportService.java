package org.motechproject.mds.service;

import org.motechproject.mds.domain.ImportExportBlueprint;
import org.motechproject.mds.domain.ImportManifest;

import java.io.Writer;

/**
 * The <code>ImportExportService</code> interface provides methods for importing and exporting
 * entities schema and data in json format.
 *
 * @see org.motechproject.mds.domain.ImportExportBlueprint
 */
public interface ImportExportService {

    /**
     * Exports entities schema and/or instances based on provided blueprint.
     * @param blueprint export blueprint containing information which entities to export and what to include
     * @param writer the writer used for output
     */
    void exportEntities(ImportExportBlueprint blueprint, Writer writer);

    /**
     * Imports entities schema and/or instances from previously stored import file.
     * @param importId previously saved import file id
     * @param blueprint import blueprint containing information which entities to import and what to include
     */
    void importEntities(String importId, ImportExportBlueprint blueprint);

    /**
     * Saves uploaded import file to temporary location, validates it and extracts {@link org.motechproject.mds.domain.ImportManifest}
     * from it.
     * @param bytes binary file representation
     * @return import manifest extracted from saved file
     */
    ImportManifest saveImportFileAndExtractManifest(byte[] bytes);
}
