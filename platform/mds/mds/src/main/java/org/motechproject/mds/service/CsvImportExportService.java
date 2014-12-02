package org.motechproject.mds.service;

import java.io.Reader;
import java.io.Writer;

/**
 * Service for exporting and importing entity data in CSV format.
 */
public interface CsvImportExportService {

    long exportCsv(long entityId, Writer writer);

    long exportCsv(String entityClassName, Writer writer);

    long importCsv(long entityId, Reader reader);

    long importCsv(String entityClassName, Reader reader);
}
