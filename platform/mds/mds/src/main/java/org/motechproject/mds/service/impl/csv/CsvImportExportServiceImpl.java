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
    private EntityService entityService;

    @Autowired
    private OsgiEventProxy osgiEventProxy;

    @Override
    public long exportCsv(long entityId, String lookupName, QueryParams params, List<String> headers, Map<String, Object> lookupFields, Writer writer) {
        LOGGER.debug("Exporting instances of entity with ID: {}", entityId);
        return csvImporterExporter.exportCsv(entityId, lookupName, params, headers, lookupFields, writer);
    }

    @Override
    public long exportCsv(long entityId, Writer writer) {
        LOGGER.debug("Exporting instances of entity with ID: {}", entityId);
        return csvImporterExporter.exportCsv(entityId, writer);
    }

    @Override
    public long exportCsv(long entityId, Writer writer, CsvExportCustomizer exportCustomizer) {
        LOGGER.debug("Exporting instances of entity with ID: {}", entityId);
        return csvImporterExporter.exportCsv(entityId, writer, exportCustomizer);
    }

    @Override
    public long exportCsv(String entityClassName, Writer writer) {
        LOGGER.debug("Exporting instances of entity: {}", entityClassName);
        return csvImporterExporter.exportCsv(entityClassName, writer);
    }


    @Override
    public CsvImportResults importCsv(long entityId, Reader reader, String fileName) {
        return importCsv(entityId, reader, fileName, new DefaultCsvImportCustomizer());
    }

    @Override
    public CsvImportResults importCsv(long entityId, Reader reader, String fileName,
                                      CsvImportCustomizer importCustomizer) {
        LOGGER.debug("Importing instances of entity with ID: {}", entityId);

        CsvImportResults importResults;
        try {
            importResults = csvImporterExporter.importCsv(entityId, reader, importCustomizer);
        } catch (RuntimeException e) {
            EntityDto entity = entityService.getEntity(entityId);
            sendImportFailureEvent(entity, fileName, e);
            throw e;
        }

        sendImportSuccessEvent(importResults, fileName);

        return importResults;
    }

    @Override
    public CsvImportResults importCsv(String entityClassName, Reader reader, String fileName) {
        LOGGER.debug("Importing instances of entity: {}", entityClassName);

        CsvImportResults importResults;
        try {
            importResults = csvImporterExporter.importCsv(entityClassName, reader);
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
}
