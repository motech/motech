package org.motechproject.mds.service.impl.csv;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.event.CrudEventBuilder;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvExportCustomizer;
import org.motechproject.mds.service.CsvImportCustomizer;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.DefaultCsvImportCustomizer;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.util.Constants;
import org.motechproject.server.osgi.event.OsgiEventProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link org.motechproject.mds.service.CsvImportExportService}.
 * Uses the SuperCSV library for handling CSV files.
 * {@link CsvImporterExporter} is used for handling import/export logic.
 * This service implementation also fires MOTECH events upon import completion or import failure.
 * This bean lives in the context of the generated MDS entities bundle.
 *
 * @see CsvImporterExporter
 */
public class CsvImportExportServiceImpl implements CsvImportExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvImportExportServiceImpl.class);

    @Autowired
    private CsvImporterExporter csvImporterExporter;

    @Autowired
    private PdfCsvExporter pdfCsvExporter;

    @Autowired
    private EntityService entityService;

    @Autowired
    private OsgiEventProxy osgiEventProxy;

    @Override
    public long exportCsv(long entityId, Writer writer) {
        logCsvExport(entityId);
        return csvImporterExporter.exportCsv(entityId, writer);
    }

    @Override
    public long exportCsv(long entityId, Writer writer, CsvExportCustomizer exportCustomizer) {
        logCsvExport(entityId);
        return csvImporterExporter.exportCsv(entityId, writer, exportCustomizer);
    }

    @Override
    public long exportCsv(long entityId, Writer writer, String lookupName, QueryParams params, List<String> headers,
                          Map<String, Object> lookupFields) {
        logCsvExport(entityId);
        return csvImporterExporter.exportCsv(entityId, writer, lookupName, params, headers, lookupFields);
    }

    @Override
    public long exportCsv(long entityId, Writer writer, String lookupName, QueryParams params, List<String> headers,
                          Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        logCsvExport(entityId);
        return csvImporterExporter.exportCsv(entityId, writer, lookupName, params, headers, lookupFields, exportCustomizer);
    }

    @Override
    public long exportCsv(String entityClassName, Writer writer) {
        logCsvExport(entityClassName);
        return csvImporterExporter.exportCsv(entityClassName, writer);
    }

    @Override
    public long exportCsv(String entityClassName, Writer writer, CsvExportCustomizer exportCustomizer) {
        logCsvExport(entityClassName);
        return csvImporterExporter.exportCsv(entityClassName, writer, exportCustomizer);
    }

    @Override
    public long exportCsv(String entityClassName, Writer writer, String lookupName, QueryParams params, List<String> headers, Map<String, Object> lookupFields) {
        logCsvExport(entityClassName);
        return csvImporterExporter.exportCsv(entityClassName, writer, lookupName, params, headers, lookupFields);
    }

    @Override
    public long exportCsv(String entityClassName, Writer writer, String lookupName, QueryParams params, List<String> headers, Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        logCsvExport(entityClassName);
        return csvImporterExporter.exportCsv(entityClassName, writer, lookupName, params, headers, lookupFields, exportCustomizer);
    }

    @Override
    public long exportPdf(long entityId, OutputStream outputStream) {
        logPdfExport(entityId);
        return pdfCsvExporter.exportPdf(entityId, outputStream);
    }

    @Override
    public long exportPdf(String entityClassName, OutputStream outputStream) {
        logPdfExport(entityClassName);
        return pdfCsvExporter.exportPdf(entityClassName, outputStream);
    }

    @Override
    public long exportPdf(long entityId, OutputStream outputStream, CsvExportCustomizer exportCustomizer) {
        logPdfExport(entityId);
        return pdfCsvExporter.exportPdf(entityId, outputStream, exportCustomizer);
    }

    @Override
    public long exportPdf(String entityClassName, OutputStream outputStream, CsvExportCustomizer exportCustomizer) {
        logPdfExport(entityClassName);
        return pdfCsvExporter.exportPdf(entityClassName, outputStream, exportCustomizer);
    }

    @Override
    public long exportPdf(long entityId, OutputStream outputStream, String lookupName, QueryParams params,
                          List<String> headers, Map<String, Object> lookupFields) {
        logPdfExport(entityId);
        return pdfCsvExporter.exportPdf(entityId, outputStream, lookupName, params, headers, lookupFields);
    }

    @Override
    public long exportPdf(String entityClassName, OutputStream outputStream, String lookupName, QueryParams params,
                          List<String> headers, Map<String, Object> lookupFields) {
        logPdfExport(entityClassName);
        return pdfCsvExporter.exportPdf(entityClassName, outputStream, lookupName, params, headers, lookupFields);
    }

    @Override
    public long exportPdf(long entityId, OutputStream outputStream, String lookupName, QueryParams params,
                          List<String> headers, Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        logPdfExport(entityId);
        return pdfCsvExporter.exportPdf(entityId, outputStream, lookupName, params, headers, lookupFields,
                exportCustomizer);
    }

    @Override
    public long exportPdf(String entityClassName, OutputStream outputStream, String lookupName, QueryParams params,
                          List<String> headers, Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        logPdfExport(entityClassName);
        return pdfCsvExporter.exportPdf(entityClassName, outputStream, lookupName, params, headers, lookupFields,
                exportCustomizer);
    }

    @Override
    public CsvImportResults importCsv(long entityId, Reader reader, String fileName, boolean continueOnError, boolean clearData) {
        return importCsv(entityId, reader, fileName, new DefaultCsvImportCustomizer(), continueOnError, clearData);
    }

    @Override
    public CsvImportResults importCsv(long entityId, Reader reader, String fileName,
                                      CsvImportCustomizer importCustomizer, boolean continueOnError, boolean clearData) {
        LOGGER.debug("Importing instances of entity with ID: {}", entityId);

        CsvImportResults importResults;
        try {
            importResults = csvImporterExporter.importCsv(entityId, reader, importCustomizer, continueOnError, clearData);
        } catch (RuntimeException e) {
            EntityDto entity = entityService.getEntity(entityId);
            sendImportFailureEvent(entity, fileName, e);
            throw e;
        }

        sendImportSuccessEvent(importResults, fileName);

        return importResults;
    }

    @Override
    public CsvImportResults importCsv(String entityClassName, Reader reader, String fileName, boolean continueOnError) {
        LOGGER.debug("Importing instances of entity: {}", entityClassName);

        CsvImportResults importResults;
        try {
            importResults = csvImporterExporter.importCsv(entityClassName, reader, continueOnError);
        } catch (RuntimeException e) {
            EntityDto entity = entityService.getEntityByClassName(entityClassName);
            sendImportFailureEvent(entity, fileName, e);
            throw e;
        }

        sendImportSuccessEvent(importResults, fileName);

        return importResults;
    }

    private void sendImportFailureEvent(EntityDto entity, String fileName, RuntimeException e) {
        Map<String, Object> params = new HashMap<>();

        CrudEventBuilder.setEntityData(params, entity.getModule(), entity.getNamespace(), entity.getName(), entity.getClassName());

        params.put(Constants.MDSEvents.CSV_IMPORT_FAILURE_MSG, e.getMessage());
        params.put(Constants.MDSEvents.CSV_IMPORT_FAILURE_STACKTRACE, ExceptionUtils.getStackTrace(e));
        params.put(Constants.MDSEvents.CSV_IMPORT_FILENAME, fileName);

        String subject = CrudEventBuilder.createSubject(entity.getModule(), entity.getNamespace(), entity.getName(),
                Constants.MDSEvents.CSV_IMPORT_FAILURE);

        osgiEventProxy.sendEvent(subject, params);
    }


    private void sendImportSuccessEvent(CsvImportResults importResults, String fileName) {
        Map<String, Object> params = new HashMap<>();

        String entityModule = importResults.getEntityModule();
        String entityNamespace = importResults.getEntityNamespace();
        String entityName = importResults.getEntityName();
        String entityClassName = importResults.getEntityClassName();

        CrudEventBuilder.setEntityData(params, entityModule, entityNamespace, entityName, entityClassName);

        params.put(Constants.MDSEvents.CSV_IMPORT_CREATED_IDS, importResults.getNewInstanceIDs());
        params.put(Constants.MDSEvents.CSV_IMPORT_UPDATED_IDS, importResults.getUpdatedInstanceIDs());
        params.put(Constants.MDSEvents.CSV_IMPORT_CREATED_COUNT, importResults.newInstanceCount());
        params.put(Constants.MDSEvents.CSV_IMPORT_UPDATED_COUNT, importResults.updatedInstanceCount());
        params.put(Constants.MDSEvents.CSV_IMPORT_TOTAL_COUNT, importResults.totalNumberOfImportedInstances());
        params.put(Constants.MDSEvents.CSV_IMPORT_FILENAME, fileName);

        String subject = CrudEventBuilder.createSubject(entityModule, entityNamespace, entityName,
                Constants.MDSEvents.CSV_IMPORT_SUCCESS);

        osgiEventProxy.sendEvent(subject, params);
    }

    private void logCsvExport(long entityId) {
        LOGGER.debug("Exporting instances of entity with ID: {} to a CSV File", entityId);
    }

    private void logCsvExport(String entityClassName) {
        LOGGER.debug("Exporting instances of entity: {} to a CSV File", entityClassName);
    }

    private void logPdfExport(long entityId) {
        LOGGER.debug("Exporting instances of entity with ID: {} to a PDF File", entityId);
    }

    private void logPdfExport(String entityClassName) {
        LOGGER.debug("Exporting instances of entity: {} to a PDF File", entityClassName);
    }
}
