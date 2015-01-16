package org.motechproject.mds.service.impl;

import com.google.gson.stream.JsonWriter;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.ImportExportBlueprint;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.ImportExportException;
import org.motechproject.mds.json.EntityWriter;
import org.motechproject.mds.json.InstanceWriter;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.ImportExportService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.helper.DataServiceHelper;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Writer;

/**
 * Implementation of {@link org.motechproject.mds.service.ImportExportService}.
 *
 * @see org.motechproject.mds.domain.ImportExportBlueprint
 * @see org.motechproject.mds.json.EntityWriter
 * @see org.motechproject.mds.json.InstanceWriter
 * @see com.google.gson.stream.JsonWriter
 */
@Service
public class ImportExportServiceImpl implements ImportExportService {

    private BundleContext bundleContext;
    private AllEntities allEntities;

    @Override
    @Transactional
    public void exportEntities(ImportExportBlueprint blueprint, Writer writer) {
        try (JsonWriter jsonWriter = new JsonWriter(writer)) {
            jsonWriter.setIndent("  ");
            jsonWriter.beginArray();
            EntityWriter entityWriter = new EntityWriter(jsonWriter);
            for (ImportExportBlueprint.Record exportRecord : blueprint) {
                exportEntity(exportRecord, jsonWriter, entityWriter);
            }
            jsonWriter.endArray();
        } catch (IOException e) {
            throw new ImportExportException("IO error occurred during writing json", e);
        }
    }

    private void exportEntity(ImportExportBlueprint.Record exportRecord, JsonWriter jsonWriter, EntityWriter entityWriter) throws IOException {
        Entity entity = getEntity(exportRecord);

        jsonWriter.beginObject();
        jsonWriter.name("entity").value(entity.getClassName());
        jsonWriter.name("type").value(entity.isDDE() ? "DDE" : "EUDE");

        jsonWriter.name("schema");
        entityWriter.writeEntity(entity);

        if (exportRecord.isIncludeData()) {
            jsonWriter.name("instances");
            exportInstances(jsonWriter, entity);
        }
        jsonWriter.endObject();
    }

    private void exportInstances(JsonWriter jsonWriter, Entity entity) throws IOException {
        MotechDataService dataService = getServiceForEntity(entity);
        InstanceWriter instanceWriter = new InstanceWriter(jsonWriter, entity, dataService);
        jsonWriter.beginArray();
        for (Object instance : dataService.retrieveAll()) {
            instanceWriter.writeInstance(instance);
        }
        jsonWriter.endArray();
    }

    private Entity getEntity(ImportExportBlueprint.Record record) {
        Entity entity = allEntities.retrieveByClassName(record.getEntityName());
        if (null == entity) {
            throw new EntityNotFoundException();
        }
        return entity;
    }

    private MotechDataService getServiceForEntity(Entity entity) {
        return DataServiceHelper.getDataService(bundleContext, entity.getClassName());
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }
}
