package org.motechproject.mds.service.impl.csv;

import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvExportCustomizer;
import org.motechproject.mds.service.DefaultCsvExportCustomizer;
import org.motechproject.mds.service.impl.csv.writer.PdfTableWriter;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * A class exporting CSV-like tables in PDF format.
 */
public class PdfCsvExporter extends AbstractMdsExporter {

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param outputStream the output stream that will be used for writing the file
     * @return number of exported instances
     */
    @Transactional
    public long exportPdf(final long entityId, final OutputStream outputStream) {
        return exportPdf(entityId, outputStream, new DefaultCsvExportCustomizer());
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName the class name of the entity for which instances will be imported
     * @param outputStream the output stream that will be used for writing the file
     * @return number of exported instances
     */
    @Transactional
    public long exportPdf(final String entityClassName, final OutputStream outputStream) {
        return exportPdf(entityClassName, outputStream, new DefaultCsvExportCustomizer());
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param outputStream the output stream that will be used for writing the file
     * @param exportCustomizer the customizer that will be used during export
     * @return number of exported instances
     */
    @Transactional
    public long exportPdf(final long entityId, OutputStream outputStream, final CsvExportCustomizer exportCustomizer) {
        Entity entity = getEntity(entityId);
        try (PdfTableWriter tableWriter = new PdfTableWriter(outputStream)) {
            return exportData(entity, tableWriter, exportCustomizer);
        }
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName the class name of the entity for which instances will be imported
     * @param outputStream the output stream that will be used for writing the file
     * @param exportCustomizer the customizer that will be used during export
     * @return number of exported instances
     */
    @Transactional
    public long exportPdf(final String entityClassName, OutputStream outputStream, final CsvExportCustomizer exportCustomizer) {
        Entity entity = getEntity(entityClassName);
        try (PdfTableWriter tableWriter = new PdfTableWriter(outputStream)) {
            return exportData(entity, tableWriter, exportCustomizer);
        }
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param outputStream the output stream that will be used for writing the file
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @return number of exported instances
     */
    @Transactional
    public long exportPdf(long entityId, OutputStream outputStream, String lookupName, QueryParams params,
                          List<String> headers, Map<String, Object> lookupFields) {
        return exportPdf(entityId, outputStream, lookupName, params, headers, lookupFields,
                new DefaultCsvExportCustomizer());
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName the class name of the entity for which instances will be imported
     * @param outputStream the output stream that will be used for writing the file
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @return number of exported instances
     */
    @Transactional
    public long exportPdf(String entityClassName, OutputStream outputStream, String lookupName, QueryParams params,
                          List<String> headers, Map<String, Object> lookupFields) {
        return exportPdf(entityClassName, outputStream, lookupName, params, headers, lookupFields,
                new DefaultCsvExportCustomizer());
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param outputStream the output stream that will be used for writing the file
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @param exportCustomizer the customizer that will be used during export
     * @return number of exported instances
     */
    @Transactional
    public long exportPdf(long entityId, OutputStream outputStream, String lookupName, QueryParams params,
                          List<String> headers, Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        Entity entity = getEntity(entityId);
        try (PdfTableWriter tableWriter = new PdfTableWriter(outputStream)){
            return exportData(entity, tableWriter, lookupName, params, headers.toArray(new String[headers.size()]),
                    lookupFields, exportCustomizer);
        }
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName the class name of the entity for which instances will be imported
     * @param outputStream the output stream that will be used for writing the file
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @param exportCustomizer the customizer that will be used during export
     * @return number of exported instances
     */
    @Transactional
    public long exportPdf(String entityClassName, OutputStream outputStream, String lookupName, QueryParams params,
                          List<String> headers, Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        Entity entity = getEntity(entityClassName);
        try (PdfTableWriter tableWriter = new PdfTableWriter(outputStream)){
            return exportData(entity, tableWriter, lookupName, params, headers.toArray(new String[headers.size()]),
                    lookupFields, exportCustomizer);
        }
    }
}
