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
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.BrowsingSettingsDto;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.UIDisplayFieldComparator;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.entityinfo.EntityInfoReader;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvImportCustomizer;
import org.motechproject.mds.service.DefaultCsvExportCustomizer;
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
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
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
    private static final int FIELD_COUNT = 13;
    private static final DateTime NOW = DateTime.now();
    private static final boolean CONTINUE_ON_ERROR = false;

    @InjectMocks
    private CsvImporterExporter csvImporterExporter = new CsvImporterExporter();

    @Mock
    private BundleContext bundleContext;

    @Mock
    private EntityInfoReader entityInfoReader;

    @Mock
    private EntityInfo entityInfo;

    @Mock
    private AdvancedSettingsDto advancedSettingsDto;

    @Mock
    private BrowsingSettingsDto browsingSettingsDto;

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
    private DefaultCsvExportCustomizer csvExportCustomizer;

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

        when(entityInfoReader.getEntityInfo(ENTITY_CLASSNAME)).thenReturn(entityInfo);
        when(entityInfoReader.getEntityInfo(ENTITY_ID)).thenReturn(entityInfo);

        when(entityInfo.getEntity()).thenReturn(entityDto);
        when(entityDto.getClassName()).thenReturn(ENTITY_CLASSNAME);
        when(entityDto.getName()).thenReturn(ENTITY_NAME);
        when(entityDto.getModule()).thenReturn(ENTITY_MODULE);
        when(entityDto.getNamespace()).thenReturn(ENTITY_NAMESPACE);

        when(csvExportCustomizer.columnOrderComparator(any(BrowsingSettingsDto.class))).thenReturn(new UIDisplayFieldComparator(new ArrayList<Number>()));
        when(csvExportCustomizer.exportDisplayName(any(FieldDto.class))).thenCallRealMethod();

        CsvTestHelper.mockRecord2Fields(entityInfo, advancedSettingsDto, browsingSettingsDto);
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

        verify(csvExportCustomizer, times(13 * INSTANCE_COUNT)).formatField(any(FieldDto.class), anyObject());

        assertEquals(INSTANCE_COUNT, result);
    }

    @Test
    public void shouldExportInstancesFromTableAsCsv() {
        when(mdsLookupService.<Record2>findMany((any(String.class)), eq("lookup"), any(Map.class), any(QueryParams.class))).thenReturn(testInstances(IdMode.INCLUDE_ID));
        StringWriter writer = new StringWriter();

        List<String> headers = Arrays.asList("ID", "Creator", "Owner", "Modified By", "Creation date", "Modification date",
                "Value Disp", "Date disp", "dateIgnoredByRest disp", "enumField Disp", "enumListField Disp", "singleRelationship Disp", "multiRelationship Disp");

        long result = csvImporterExporter.exportCsv(ENTITY_ID, writer, "lookup", null, headers, null);

        assertEquals(INSTANCE_COUNT, result);
        assertEquals(getTestEntityRecordsAsCsv(IdMode.INCLUDE_ID), writer.toString());
    }

    @Test
    public void shouldImportEntitiesWithIdFromCsv() {
        testImport(IdMode.INCLUDE_ID, false);
    }

    @Test
    public void shouldImportEntitiesFromCsvWithNotAllFields() {
        testImport(IdMode.NO_ID_COLUMN, false);
    }

    @Test
    public void shouldImportNewEntitiesFromCsv() {
        testImport(IdMode.EMPTY_ID_COLUMN, false);
    }

    @Test
    public void shouldClearDataAndImportEntitiesWithIdFromCsv() {
        testImport(IdMode.INCLUDE_ID, true);
    }

    @Test
    public void shouldClearDataAndImportEntitiesFromCsvWithNotAllFields() {
        testImport(IdMode.NO_ID_COLUMN, true);
    }

    @Test
    public void shouldClearDataAndImportNewEntitiesFromCsv() {
        testImport(IdMode.EMPTY_ID_COLUMN, true);
    }

    @Test
    public void shouldUseImportCustomizer() {
        StringReader reader = new StringReader(getTestEntityRecordsAsCsv(IdMode.EMPTY_ID_COLUMN));

        when(csvImportCustomizer.doCreate(any(Record2.class), eq(motechDataService))).thenAnswer(new CreateAnswer());

        CsvImportResults results = csvImporterExporter.importCsv(ENTITY_ID, reader, csvImportCustomizer, CONTINUE_ON_ERROR, false);

        ArgumentCaptor<Record2> captor = ArgumentCaptor.forClass(Record2.class);
        verify(csvImportCustomizer, times(INSTANCE_COUNT)).findExistingInstance(anyMap(), eq(motechDataService));
        verify(csvImportCustomizer, times(INSTANCE_COUNT)).doCreate(captor.capture(), eq(motechDataService));
        verify(csvImportCustomizer, times(FIELD_COUNT)).findField(anyString(), anyList());
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
        results = csvImporterExporter.importCsv(ENTITY_ID, reader, true, false);

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
            csvImporterExporter.importCsv(ENTITY_ID, reader2, false, false);
        } catch (RuntimeException e){
            thrown = true;
        }
        // First invalid row encountered should stop whole import process and throw an exception
        assertTrue(thrown);
    }

    private void testImport(IdMode idMode, boolean clearData) {
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

        CsvImportResults results = csvImporterExporter.importCsv(ENTITY_ID, reader, CONTINUE_ON_ERROR, clearData);

        ArgumentCaptor<Record2> captor = ArgumentCaptor.forClass(Record2.class);
        if (idMode == IdMode.INCLUDE_ID) {
            verify(motechDataService, times(INSTANCE_COUNT)).update(captor.capture());
        } else {
            verify(motechDataService, times(INSTANCE_COUNT)).create(captor.capture());
        }

        if (clearData) {
            verify(motechDataService).deleteAll();
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
        StringBuilder sb = new StringBuilder("Value Disp,Date disp,"); // these are UI displayable

        if ((idMode != IdMode.NO_ID_COLUMN) && (idMode != IdMode.INVALID)) {
            sb.append("ID,");
        }

        sb.append("Creator,Owner,Modified By,Creation date,");
        sb.append("Modification date,dateIgnoredByRest disp,enumField Disp,enumListField Disp");
        sb.append(",singleRelationship Disp,multiRelationship Disp\r\n");

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
