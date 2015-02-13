package org.motechproject.mds.service;

import org.motechproject.mds.dto.CsvImportResults;

import java.io.Reader;
import java.io.Writer;

/**
 * Service for exporting and importing entity data in CSV format.
 * The format is the same for both import and export.
 * Columns are separated by the ',' character. The top row(header row) consists
 * of names of the fields represented by the columns.
 */
public interface CsvImportExportService {

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @return number of exported instances
     */
    long exportCsv(long entityId, Writer writer);

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName class name of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @return number of exported instances
     */
    long exportCsv(String entityClassName, Writer writer);

    /**
     * Import instances from a CSV file
     * @param entityId id of the entity for which the instances will be imported
     * @param reader the reader that will be used for reading the file contents
     * @return IDs of instances updated/added during import
     */
    CsvImportResults importCsv(long entityId, Reader reader);

    /**
     * Import instances from a CSV file
     * @param entityClassName class name of the entity for which the instances will be imported
     * @param reader the reader that will be used for reading the file contents
     * @return IDs of instances updated/added during import
     */
    CsvImportResults importCsv(String entityClassName, Reader reader);
}
