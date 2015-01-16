package org.motechproject.mds.service;

import org.motechproject.mds.domain.ImportExportBlueprint;

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
}
