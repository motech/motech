package org.motechproject.mds.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.impl.CsvImportExportServiceImpl;
import org.motechproject.mds.testutil.Record2;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CsvImportExportServiceTest {

    private static final long ENTITY_ID = 3L;
    private static final String ENTITY_CLASSNAME = Record2.class.getName();
    private static final String DATA_SERVICE_CLASSNAME = "org.motechproject.mds.test.service.CsvEntityService";
    private static final int INSTANCE_COUNT = 20;
    private static final DateTime NOW = DateTime.now();


    @InjectMocks
    private CsvImportExportService csvImportExportService = new CsvImportExportServiceImpl();

    @Mock
    private BundleContext bundleContext;

    @Mock
    private AllEntities allEntities;

    @Mock
    private Entity entity;

    @Mock
    private ServiceReference serviceRef;

    @Mock
    private MotechDataService<Record2> motechDataService;

    @Before
    public void setUp() {
        MotechClassPool.registerServiceInterface(ENTITY_CLASSNAME, DATA_SERVICE_CLASSNAME);
        when(bundleContext.getServiceReference(DATA_SERVICE_CLASSNAME)).thenReturn(serviceRef);
        when(bundleContext.getService(serviceRef)).thenReturn(motechDataService);
        when(motechDataService.getClassType()).thenReturn(Record2.class);

        when(allEntities.retrieveById(ENTITY_ID)).thenReturn(entity);
        when(entity.getClassName()).thenReturn(ENTITY_CLASSNAME);

        mockFields();
    }

    @Test
    public void shouldExportTestEntitiesAsCsv() {
        when(motechDataService.retrieveAll()).thenReturn(testInstances(IdMode.INCLUDE_ID));
        StringWriter writer = new StringWriter();

        long result = csvImportExportService.exportCsv(ENTITY_ID, writer);

        assertEquals(INSTANCE_COUNT, result);
        assertEquals(getTestEntityRecordsAsCsv(IdMode.INCLUDE_ID), writer.toString());
    }

    @Test
    public void shouldImportEntitiesWithIdFromCsv() {
        testImport(IdMode.INCLUDE_ID);
    }

    @Test
    public void shouldImportEntitiesFromCsvWithNotAllFields() {
        testImport(IdMode.NO_ID_COLUMN);
    }

    @Test
    public void shouldImportNewEntitiesFromCsv() {
        testImport(IdMode.EMPTY_ID_COLUMN);
    }

    private void testImport(IdMode idMode) {
        StringReader reader = new StringReader(getTestEntityRecordsAsCsv(idMode));

        long result = csvImportExportService.importCsv(ENTITY_ID, reader);

        ArgumentCaptor<Record2> captor = ArgumentCaptor.forClass(Record2.class);
        if (idMode == IdMode.INCLUDE_ID) {
            verify(motechDataService, times(INSTANCE_COUNT)).updateFromTransient(captor.capture());
        } else {
            verify(motechDataService, times(INSTANCE_COUNT)).create(captor.capture());
        }

        assertEquals(INSTANCE_COUNT, result);
        assertEquals(testInstances(idMode), captor.getAllValues());
    }

    private void mockFields() {
        List<Field> fields = new ArrayList<>();

        fields.add(new Field(entity, "id", "ID", new Type(Long.class)));
        fields.add(new Field(entity, "creator", "Creator", new Type(String.class)));
        fields.add(new Field(entity, "owner", "Owner", new Type(String.class)));
        fields.add(new Field(entity, "modifiedBy", "Modified By", new Type(String.class)));
        fields.add(new Field(entity, "creationDate", "Creation date", new Type(DateTime.class)));
        fields.add(new Field(entity, "modificationDate", "Modification date", new Type(DateTime.class)));
        fields.add(new Field(entity, "value", "Value Disp", new Type(String.class)));
        fields.add(new Field(entity, "date", "Date disp", new Type(Date.class)));
        fields.add(new Field(entity, "dateIgnoredByRest", "dateIgnoredByRest disp", new Type(Date.class)));

        when(entity.getFields()).thenReturn(fields);
    }

    private List<Record2> testInstances(IdMode idMode) {
        List<Record2> instances = new ArrayList<>();

        for (int i = 0; i < INSTANCE_COUNT; i++) {
            Record2 record = new Record2();

            if (idMode == IdMode.INCLUDE_ID) {
                record.setId((long) i);
            }

            record.setCreator("the creator " + i);
            record.setOwner("the owner " + i);
            record.setModifiedBy("username" + i);
            record.setCreationDate(NOW.plusMinutes(i));
            record.setModificationDate(NOW.plusHours(i));
            record.setValue("value " + i);
            record.setDate(NOW.plusSeconds(i).toDate());
            record.setDateIgnoredByRest(NOW.minusHours(i).toDate());

            instances.add(record);
        }

        return instances;
    }

    private String getTestEntityRecordsAsCsv(IdMode idMode) {
        StringBuilder sb = new StringBuilder(idMode == IdMode.NO_ID_COLUMN ? "" : "id,");
        sb.append("creator,owner,modifiedBy,creationDate,")
            .append("modificationDate,value,date,dateIgnoredByRest\r\n");

        for (int i = 0; i < INSTANCE_COUNT; i++) {
            if (idMode == IdMode.INCLUDE_ID) {
                sb.append(i);
            }
            if (idMode != IdMode.NO_ID_COLUMN) {
                sb.append(',');
            }
            sb.append("the creator ").append(i).append(',');
            sb.append("the owner ").append(i).append(',').append("username").append(i).append(',');
            sb.append(NOW.plusMinutes(i)).append(',').append(NOW.plusHours(i)).append(',');
            sb.append("value ").append(i).append(',').append(NOW.plusSeconds(i)).append(',');
            sb.append(NOW.minusHours(i)).append("\r\n");
        }

        return sb.toString();
    }

    private enum IdMode {
        INCLUDE_ID, EMPTY_ID_COLUMN, NO_ID_COLUMN
    }
}
