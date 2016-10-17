package org.motechproject.mds.service;

import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.query.QueryParams;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

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
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param exportCustomizer customizer, that allows to adjust CSV file output
     * @return number of exported instances
     */
    long exportCsv(long entityId, Writer writer, CsvExportCustomizer exportCustomizer);

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName class name of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param exportCustomizer customizer, that allows to adjust CSV file output
     * @return number of exported instances
     */
    long exportCsv(String entityClassName, Writer writer, CsvExportCustomizer exportCustomizer);

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @return number of exported instances
     */
    long exportCsv(long entityId, Writer writer, String lookupName, QueryParams params, List<String> headers,
                   Map<String, Object> lookupFields);

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName class name of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @return number of exported instances
     */
    long exportCsv(String entityClassName, Writer writer, String lookupName, QueryParams params, List<String> headers,
                   Map<String, Object> lookupFields);

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @param exportCustomizer customizer, that allows to adjust CSV file output
     * @return number of exported instances
     */
    long exportCsv(long entityId, Writer writer, String lookupName, QueryParams params, List<String> headers,
                   Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer);

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName class name of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @param exportCustomizer customizer, that allows to adjust CSV file output
     * @return number of exported instances
     */
    long exportCsv(String entityClassName, Writer writer, String lookupName, QueryParams params, List<String> headers,
                   Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer);

    /**
     * Exports entity instances to a PDF file.
     * @param entityId id of the entity for which the instances will be exported
     * @param outputStream the stream to write the PDF to
     * @return number of exported instances
     */
    long exportPdf(long entityId, OutputStream outputStream);

    /**
     * Exports entity instances to a PDF file.
     * @param entityClassName class name of the entity for which the instances will be exported
     * @param outputStream the stream to write the PDF to
     * @return number of exported instances
     */
    long exportPdf(String entityClassName, OutputStream outputStream);

    /**
     * Exports entity instances to a PDF file.
     * @param entityId id of the entity for which the instances will be exported
     * @param outputStream the stream to write the PDF to
     * @param exportCustomizer customizer, that allows to adjust PDF file output
     * @return number of exported instances
     */
    long exportPdf(long entityId, OutputStream outputStream, CsvExportCustomizer exportCustomizer);

    /**
     * Exports entity instances to a PDF file.
     * @param entityClassName class name of the entity for which the instances will be exported
     * @param outputStream the writer that will be used for output
     * @param exportCustomizer customizer, that allows to adjust PDF file output
     * @return number of exported instances
     */
    long exportPdf(String entityClassName, OutputStream outputStream, CsvExportCustomizer exportCustomizer);

    /**
     * Exports entity instances to a PDF file.
     * @param entityId id of the entity for which the instances will be exported
     * @param outputStream the stream to write the PDF to
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @return number of exported instances
     */
    long exportPdf(long entityId, OutputStream outputStream, String lookupName, QueryParams params, List<String> headers,
                   Map<String, Object> lookupFields);

    /**
     * Exports entity instances to a PDF file.
     * @param entityClassName class name of the entity for which the instances will be exported
     * @param outputStream the stream to write the PDF to
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @return number of exported instances
     */
    long exportPdf(String entityClassName, OutputStream outputStream, String lookupName, QueryParams params, List<String> headers,
                   Map<String, Object> lookupFields);

    /**
     * Exports entity instances to a PDF file.
     * @param entityId id of the entity for which the instances will be exported
     * @param outputStream the stream to write the PDF to
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @param exportCustomizer customizer, that allows to adjust PDF file output
     * @return number of exported instances
     */
    long exportPdf(long entityId, OutputStream outputStream, String lookupName, QueryParams params, List<String> headers,
                   Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer);

    /**
     * Exports entity instances to a PDF file.
     * @param entityClassName class name of the entity for which the instances will be exported
     * @param outputStream the stream to write the PDF to
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @param exportCustomizer customizer, that allows to adjust PDF file output
     * @return number of exported instances
     */
    long exportPdf(String entityClassName, OutputStream outputStream, String lookupName, QueryParams params, List<String> headers,
                   Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer);

    /**
     * Import instances from a CSV file
     * @param entityId id of the entity for which the instances will be imported
     * @param reader the reader that will be used for reading the file contents
     * @param fileName the name of the CSV file
     * @param continueOnError if true, import will continue with next row if exception was encountered,
     *                        if false, import process will stop and rethrow the exception
     * @param clearData if true, import will clear instances from table
     * @return IDs of instances updated/added during import
     */
    CsvImportResults importCsv(long entityId, Reader reader, String fileName, boolean continueOnError, boolean clearData);

    /**
     * Import instances from a CSV file
     * @param entityId id of the entity for which the instances will be imported
     * @param reader the reader that will be used for reading the file contents
     * @param fileName the name of the CSV file
     * @param importCustomizer the customizer that will be used during import
     * @param continueOnError if true, import will continue with next row if exception was encountered,
     *                        if false, import process will stop and rethrow the exception
     * @param clearData if true, import will clear instances from table
     * @return IDs of instances updated/added during import
     */
    CsvImportResults importCsv(long entityId, Reader reader, String fileName, CsvImportCustomizer importCustomizer, boolean continueOnError, boolean clearData);

    /**
     * Import instances from a CSV file
     * @param entityClassName class name of the entity for which the instances will be imported
     * @param reader the reader that will be used for reading the file contents
     * @param fileName the name of the CSV file
     * @param continueOnError if true, import will continue with next row if exception was encountered,
     *                        if false, import process will stop and rethrow the exception
     * @return IDs of instances updated/added during import
     */
    CsvImportResults importCsv(String entityClassName, Reader reader, String fileName, boolean continueOnError);
}
