package org.motechproject.mds.service.impl.csv;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.UIDisplayFieldComparator;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.CsvExportCustomizer;
import org.motechproject.mds.service.CsvImportCustomizer;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.records.Record2;
import org.motechproject.mds.testutil.records.RecordEnum;
import org.motechproject.mds.testutil.records.RelatedClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CsvImporterExporterTest {

    private static final long ENTITY_ID = 3L;
    private static final String ENTITY_CLASSNAME = Record2.class.getName();
    private static final String RELATED_CLASSNAME = RelatedClass.class.getName();
    private static final String DATA_SERVICE_CLASSNAME = "org.motechproject.mds.test.service.CsvEntityService";
    private static final String RELATED_SERVICE_CLASSNAME = "org.motechproject.mds.test.service.RelatedService";
    private static final String ENTITY_MODULE = "module";
    private static final String ENTITY_NAMESPACE = "emns";
    private static final String ENTITY_NAME = "Record2";
    private static final int INSTANCE_COUNT = 20;
    private static final DateTime NOW = DateTime.now();
    private static final boolean CONTINUE_ON_ERROR = false;

    @InjectMocks
    private CsvImporterExporter csvImporterExporter = new CsvImporterExporter();

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

    @Mock
    private ServiceReference relatedServiceRef;

    @Mock
    private MotechDataService<RelatedClass> relatedDataService;

    @Mock
    private EntityDto entityDto;

    @Mock
    private CsvImportCustomizer csvImportCustomizer;

    @Mock
    private CsvExportCustomizer csvExportCustomizer;

    @Mock
    private MDSLookupService mdsLookupService;

    @Before
    public void setUp() {
        MotechClassPool.registerServiceInterface(ENTITY_CLASSNAME, DATA_SERVICE_CLASSNAME);
        when(bundleContext.getServiceReference(DATA_SERVICE_CLASSNAME)).thenReturn(serviceRef);
        when(bundleContext.getService(serviceRef)).thenReturn(motechDataService);
        when(motechDataService.getClassType()).thenReturn(Record2.class);

        MotechClassPool.registerServiceInterface(RELATED_CLASSNAME, RELATED_SERVICE_CLASSNAME);
        when(bundleContext.getServiceReference(RELATED_SERVICE_CLASSNAME)).thenReturn(relatedServiceRef);
        when(bundleContext.getService(relatedServiceRef)).thenReturn(relatedDataService);
        when(relatedDataService.getClassType()).thenReturn(RelatedClass.class);

        when(relatedDataService.findById(0L)).thenReturn(new RelatedClass(0L));
        when(relatedDataService.findById(1L)).thenReturn(new RelatedClass(1L));

        when(allEntities.retrieveById(ENTITY_ID)).thenReturn(entity);

        when(entity.toDto()).thenReturn(entityDto);
        when(entityDto.getClassName()).thenReturn(ENTITY_CLASSNAME);
        when(entityDto.getName()).thenReturn(ENTITY_NAME);
        when(entityDto.getModule()).thenReturn(ENTITY_MODULE);
        when(entityDto.getNamespace()).thenReturn(ENTITY_NAMESPACE);

        when(csvExportCustomizer.columnOrderComparator()).thenReturn(new UIDisplayFieldComparator());

        CsvTestHelper.mockRecord2Fields(entity);
    }

    @Test
    public void shouldExportAllInstancesAsCsv() {
        when(motechDataService.retrieveAll(any(QueryParams.class))).thenReturn(testInstances(IdMode.INCLUDE_ID));
        StringWriter writer = new StringWriter();

        long result = csvImporterExporter.exportCsv(ENTITY_ID, writer);

        assertEquals(INSTANCE_COUNT, result);
        assertEquals(getTestEntityRecordsAsCsv(IdMode.INCLUDE_ID), writer.toString());
    }

    @Test
    public void shouldUseExportCustomizer() {
        when(motechDataService.retrieveAll(any(QueryParams.class))).thenReturn(testInstances(IdMode.INCLUDE_ID));
        StringWriter writer = new StringWriter();

        long result = csvImporterExporter.exportCsv(ENTITY_ID, writer, csvExportCustomizer);

        verify(csvExportCustomizer, times(2 * INSTANCE_COUNT)).formatRelationship(anyObject());

        assertEquals(INSTANCE_COUNT, result);
    }

    @Test
    public void shouldExportInstancesFromTableAsCsv() {
        when(mdsLookupService.findMany((any(String.class)), eq("lookup"), any(Map.class), any(QueryParams.class))).thenReturn(testInstances(IdMode.INCLUDE_ID));
        StringWriter writer = new StringWriter();

        List<String> headers = Arrays.asList("id", "creator", "owner", "modifiedBy", "creationDate", "modificationDate",
                "value", "date", "dateIgnoredByRest", "enumField", "enumListField", "singleRelationship", "multiRelationship");

        long result = csvImporterExporter.exportCsv(ENTITY_ID, writer, "lookup", null, headers, null);

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

    @Test
    public void shouldUseImportCustomizer() {
        StringReader reader = new StringReader(getTestEntityRecordsAsCsv(IdMode.EMPTY_ID_COLUMN));

        when(csvImportCustomizer.doCreate(any(Record2.class), eq(motechDataService))).thenAnswer(new CreateAnswer());

        CsvImportResults results = csvImporterExporter.importCsv(ENTITY_ID, reader, csvImportCustomizer, CONTINUE_ON_ERROR);

        ArgumentCaptor<Record2> captor = ArgumentCaptor.forClass(Record2.class);
        verify(csvImportCustomizer, times(INSTANCE_COUNT)).findExistingInstance(anyMap(), eq(motechDataService));
        verify(csvImportCustomizer, times(INSTANCE_COUNT)).doCreate(captor.capture(), eq(motechDataService));
        verify(csvImportCustomizer, never()).doUpdate(captor.capture(), eq(motechDataService));

        assertNotNull(results);
        assertEquals(INSTANCE_COUNT, results.totalNumberOfImportedInstances());
    }

    @Test
    public void testImportWithInvalidRows() {
        CsvImportResults results;

        // This will provide csv import with 3 rows with invalid enum fields
        StringReader reader = new StringReader(getTestEntityRecordsAsCsv(IdMode.INVALID));

        // First import call with continueOnError flag on
        results = csvImporterExporter.importCsv(ENTITY_ID, reader, true);

        // Check how many times create was called, how many objects were created and how many errors were caught
        // Expecting 17 creates and 3 errors since we got 3 invalid rows in a set of 20 passed as import input
        ArgumentCaptor<Record2> captor = ArgumentCaptor.forClass(Record2.class);
        verify(motechDataService, times(17)).create(captor.capture());
        assertNotNull(results);
        assertEquals(17, results.totalNumberOfImportedInstances());
        assertEquals(3, results.getRowErrors().size());

        // Now call import with continueOnError flag off
        StringReader reader2 = new StringReader(getTestEntityRecordsAsCsv(IdMode.INVALID));
        boolean thrown = false;
        try {
            csvImporterExporter.importCsv(ENTITY_ID, reader2, false);
        } catch (RuntimeException e){
            thrown = true;
        }
        // First invalid row encountered should stop whole import process and throw an exception
        assertTrue(thrown);
    }

    private void testImport(IdMode idMode) {
        StringReader reader = new StringReader(getTestEntityRecordsAsCsv(idMode));
        // if id provided, prepare entities that will be updated
        if (idMode == IdMode.INCLUDE_ID) {
            when(motechDataService.update(any(Record2.class))).thenAnswer(new Answer<Record2>() {
                @Override
                public Record2 answer(InvocationOnMock invocation) throws Throwable {
                    return (Record2) invocation.getArguments()[0];
                }
            });
            for (long i = 0; i < INSTANCE_COUNT; i++) {
                when(motechDataService.findById(i)).thenReturn(new Record2());
            }
        } else {
            when(motechDataService.create(any(Record2.class))).thenAnswer(new CreateAnswer());
        }

        CsvImportResults results = csvImporterExporter.importCsv(ENTITY_ID, reader, CONTINUE_ON_ERROR);

        ArgumentCaptor<Record2> captor = ArgumentCaptor.forClass(Record2.class);
        if (idMode == IdMode.INCLUDE_ID) {
            verify(motechDataService, times(INSTANCE_COUNT)).update(captor.capture());
        } else {
            verify(motechDataService, times(INSTANCE_COUNT)).create(captor.capture());
        }

        assertNotNull(results);
        assertEquals(INSTANCE_COUNT, results.totalNumberOfImportedInstances());
        assertEquals(testInstances(idMode), captor.getAllValues());

        assertEquals(ENTITY_CLASSNAME, results.getEntityClassName());
        assertEquals(ENTITY_NAME, results.getEntityName());
        assertEquals(ENTITY_MODULE, results.getEntityModule());
        assertEquals(ENTITY_NAMESPACE, results.getEntityNamespace());
        assertEquals(0, results.getRowErrors().size());

        if (idMode == IdMode.INCLUDE_ID) {
            assertEquals(INSTANCE_COUNT, results.updatedInstanceCount());
            assertEquals(0, results.newInstanceCount());
            assertTrue(results.getNewInstanceIDs().isEmpty());
            assertEquals(listFromRangeInclusive(0, 19), results.getUpdatedInstanceIDs());
        } else {
            assertEquals(INSTANCE_COUNT, results.newInstanceCount());
            assertEquals(0, results.updatedInstanceCount());
            assertTrue(results.getUpdatedInstanceIDs().isEmpty());
            assertEquals(listFromRangeInclusive(0, 19), results.getNewInstanceIDs());
        }
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
            record.setEnumField(enumValue(i));
            record.setEnumListField(Arrays.asList(enumValue(i + 1), enumValue(i + 2)));
            record.setSingleRelationship(new RelatedClass(relatedId(i)));
            record.setMultiRelationship(new ArrayList<>((Arrays.asList(new RelatedClass(relatedId(i + 1)),
                    new RelatedClass(relatedId(i + 2))))));

            instances.add(record);
        }

        return instances;
    }

    private String getTestEntityRecordsAsCsv(IdMode idMode) {
        StringBuilder sb = new StringBuilder("value,date,"); // these are UI displayable

        if ((idMode != IdMode.NO_ID_COLUMN) && (idMode != IdMode.INVALID)) {
            sb.append("id,");
        }

        sb.append("creator,owner,modifiedBy,creationDate,");
        sb.append("modificationDate,dateIgnoredByRest,enumField,enumListField");
        sb.append(",singleRelationship,multiRelationship\r\n");

        for (int i = 0; i < INSTANCE_COUNT; i++) {
            sb.append("value ").append(i).append(',').append(NOW.plusSeconds(i)).append(','); // value, date

            // id
            if (idMode == IdMode.INCLUDE_ID) {
                sb.append(i);
            }
            if ((idMode != IdMode.NO_ID_COLUMN) && (idMode != IdMode.INVALID)) {
                sb.append(',');
            }

            sb.append("the creator ").append(i).append(','); // creator
            sb.append("the owner ").append(i).append(',').append("username").append(i).append(','); // owner
            sb.append(NOW.plusMinutes(i)).append(',').append(NOW.plusHours(i)).append(','); // creationDate, modificationDate
            sb.append(NOW.minusHours(i)).append(',').append(enumValue(i)); // dateIgnoredByRest, enumField
            // enum list
            sb.append(",\"").append(enumValue(i + 1)).append(',').append(enumValue(i + 2));
            // adding invalid enum values for rows 3, 7 and 11
            if (idMode == IdMode.INVALID){
                if ((i == 3) || (i == 7) || (i == 11)) {
                    sb.append(',').append("INVALID_ENUM");
                }
            }
            sb.append("\",");
            // relationship
            sb.append(relatedId(i)).append(',');
            // relationships list
            sb.append('\"').append(relatedId(i + 1)).append(',').append(relatedId(i + 2));
            sb.append("\"\r\n");
        }

        return sb.toString();
    }

    private RecordEnum enumValue(int i) {
        // rotate between enum values
        if (i % 3 == 0) {
            return RecordEnum.ONE;
        } else if (i % 3 == 1) {
            return RecordEnum.TWO;
        } else {
            return RecordEnum.THREE;
        }
    }

    private long relatedId(int index) {
        return index % 2;
    }

    private List<Long> listFromRangeInclusive(int start, int end) {
        List<Long> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            list.add((long) i);
        }
        return list;
    }

    private class CreateAnswer implements Answer<Record2> {

        private long idCounter = 0;

        @Override
        public Record2 answer(InvocationOnMock invocation) throws Throwable {
            Record2 record = new Record2();
            record.setId(idCounter++);
            return record;
        }
    }

    private enum IdMode {
        INCLUDE_ID, EMPTY_ID_COLUMN, NO_ID_COLUMN, INVALID
    }
}
