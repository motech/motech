package org.motechproject.mds.service.impl;

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.FileUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.ImportExportBlueprint;
import org.motechproject.mds.domain.ImportManifest;
import org.motechproject.mds.exception.importexport.ImportExportException;
import org.motechproject.mds.helper.RelationshipResolver;
import org.motechproject.mds.helper.RelationshipSorter;
import org.motechproject.mds.json.ExportContext;
import org.motechproject.mds.json.ExportWriter;
import org.motechproject.mds.json.ImportContext;
import org.motechproject.mds.json.ImportManifestReader;
import org.motechproject.mds.json.ImportReader;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.repository.AllTypes;
import org.motechproject.mds.service.MdsBundleRegenerationService;
import org.motechproject.mds.service.ImportExportService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jdo.JdoTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link org.motechproject.mds.service.ImportExportService}.
 *
 * @see org.motechproject.mds.domain.ImportExportBlueprint
 * @see org.motechproject.mds.json.EntityWriter
 * @see org.motechproject.mds.json.InstancesWriter
 * @see com.google.gson.stream.JsonWriter
 */
@Service
public class ImportExportServiceImpl implements ImportExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportExportServiceImpl.class);

    private static final String IMPORT_FILE_PREFIX = "mds_import_";
    private static final String IMPORT_FILE_SUFFIX = ".tmp.json";
    private static final String TEMP_DIRECTORY_PATH = System.getProperty("java.io.tmpdir");

    private AllEntities allEntities;
    private AllTypes allTypes;
    private RelationshipResolver relationshipResolver;
    private BundleContext bundleContext;
    private JdoTransactionManager transactionManager;
    private MdsBundleRegenerationService mdsBundleRegenerationService;

    @Override
    @Transactional
    public void exportEntities(ImportExportBlueprint blueprint, Writer writer) {
        try (JsonWriter jsonWriter = new JsonWriter(writer)) {
            jsonWriter.setIndent("  ");
            ExportContext exportContext = new ExportContext(sortBlueprintRecords(blueprint), bundleContext, allEntities);
            ExportWriter exportWriter = new ExportWriter(jsonWriter, exportContext);
            exportWriter.export();
        } catch (IOException e) {
            throw new ImportExportException("An IO error occurred during export.", e);
        }
    }

    @Override
    public void importEntities(String importId, ImportExportBlueprint blueprint) {
        File file = getImportFile(importId);
        try {
            importEntities(file, blueprint);
        } catch (JsonParseException e) {
            throw new ImportExportException("Invalid JSON.", e);
        } catch (IOException e) {
            throw new ImportExportException("An IO error occurred during import.", e);
        }
    }

    private void importEntities(File file, ImportExportBlueprint blueprint) throws FileNotFoundException {
        ImportContext importContext = new ImportContext(blueprint, bundleContext, allEntities, allTypes, relationshipResolver);
        importSchema(importContext, new JsonReader(new BufferedReader(new FileReader(file))));
        importInstances(importContext, new JsonReader(new BufferedReader(new FileReader(file))));
    }

    private void importSchema(final ImportContext importContext, final JsonReader jsonReader) {
        LOGGER.debug("Importing schema...");
        doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    ImportReader importReader = new ImportReader(jsonReader, importContext);
                    importReader.importSchema();
                } catch (IOException e) {
                    throw new ImportExportException("An error occurred during importing schema", e);
                }
            }
        });
        if (importContext.hasUnresolvedEntities()) {
            LOGGER.warn("Unresolved relationships found. Skipping. ({})", importContext.getUnresolvedEntities());
        }
        LOGGER.debug("Schema imported.");
        LOGGER.debug("Regenerating MDS bundle and refreshing affected bundles: {}", importContext.getAffectedModules());
        mdsBundleRegenerationService.regenerateMdsDataBundleAfterDdeEnhancement(importContext.getAffectedModulesArray());
        LOGGER.debug("Bundles regenerated/refreshed");
    }

    private void importInstances(final ImportContext importContext, final JsonReader jsonReader) {
        LOGGER.debug("Importing instances...");
        doInTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    importContext.removeExistingInstances();
                    ImportReader importReader = new ImportReader(jsonReader, importContext);
                    importReader.importInstances();
                } catch (IOException e) {
                    throw new ImportExportException("An error occurred during importing schema", e);
                }
            }
        });
        LOGGER.debug("Instances imported.");
    }

    private ImportManifest extractManifest(byte[] bytes) throws IOException {
        JsonReader jsonReader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        ImportManifestReader manifestReader = new ImportManifestReader(jsonReader, allEntities);
        return manifestReader.readManifest();
    }

    @Override
    @Transactional
    public ImportManifest saveImportFileAndExtractManifest(byte[] bytes) {
        try {
            ImportManifest manifest = extractManifest(bytes);
            File file = createImportFile();
            FileUtils.writeByteArrayToFile(file, bytes);
            manifest.setImportId(getImportId(file));
            return manifest;
        } catch (IOException e) {
            throw new ImportExportException("Cannot save import file", e);
        }
    }

    private File createImportFile() throws IOException {
        return File.createTempFile(IMPORT_FILE_PREFIX, IMPORT_FILE_SUFFIX);
    }

    private File getImportFile(String importId) {
        String path = TEMP_DIRECTORY_PATH + File.separator + IMPORT_FILE_PREFIX + importId + IMPORT_FILE_SUFFIX;
        return new File(path);
    }

    private String getImportId(File file) {
        String fileName = file.getName();
        return fileName.substring(IMPORT_FILE_PREFIX.length(), fileName.length() - IMPORT_FILE_SUFFIX.length());
    }

    private ImportExportBlueprint sortBlueprintRecords(ImportExportBlueprint blueprint) {
        List<Entity> entities = new ArrayList<>(blueprint.size());
        for (ImportExportBlueprint.Record record : blueprint) {
            entities.add(allEntities.retrieveByClassName(record.getEntityName()));
        }
        RelationshipSorter relationshipSorter = new RelationshipSorter();
        relationshipSorter.sort(entities);
        ImportExportBlueprint sortedBlueprint = new ImportExportBlueprint();
        for (Entity entity : entities) {
            String entityName = entity.getClassName();
            sortedBlueprint.includeEntitySchema(entityName, blueprint.isIncludeEntitySchema(entityName));
            sortedBlueprint.includeEntityData(entityName, blueprint.isIncludeEntityData(entityName));
        }
        return sortedBlueprint;
    }

    private void doInTransaction(TransactionCallbackWithoutResult callback) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(callback);
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }

    @Autowired
    public void setAllTypes(AllTypes allTypes) {
        this.allTypes = allTypes;
    }

    @Autowired
    public void setRelationshipResolver(RelationshipResolver relationshipResolver) {
        this.relationshipResolver = relationshipResolver;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setTransactionManager(JdoTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Autowired
    public void setMdsBundleRegenerationService(MdsBundleRegenerationService mdsBundleRegenerationService) {
        this.mdsBundleRegenerationService = mdsBundleRegenerationService;
    }
}
