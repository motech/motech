package org.motechproject.mds.web.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.ObjectNotFoundException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.DefaultMotechDataService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.web.FieldTestHelper;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.service.InstanceService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InstanceServiceTest {

    private static final long ENTITY_ID = 11;
    private static final long INSTANCE_ID = 4;

    @InjectMocks
    private InstanceService instanceService = new InstanceServiceImpl();

    @Mock
    private EntityService entityService;

    @Mock
    private EntityDto entity;

    @Mock
    private MotechDataService motechDataService;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference serviceReference;

    @Mock
    private TrashService trashService;

    @Before
    public void setUp() {
        when(entity.getClassName()).thenReturn(TestSample.class.getName());
        when(entity.getId()).thenReturn(ENTITY_ID);
        when(bundleContext.getBundles()).thenReturn(new Bundle[0]);
    }

    @Test
    public void shouldReturnNewInstances() {
        mockSampleFields();

        EntityRecord record = instanceService.newInstance(ENTITY_ID);

        assertNotNull(record);
        assertEquals(Long.valueOf(ENTITY_ID), record.getEntitySchemaId());
        assertNull(record.getId());

        List<FieldRecord> fieldRecords = record.getFields();
        assertCommonFieldRecordFields(fieldRecords);
        assertEquals(asList("Default", 7, null, null),
                extract(fieldRecords, on(FieldRecord.class).getValue()));
    }

    @Test
    public void shouldReturnEntityInstance() {
        mockDataService();
        mockSampleFields();
        mockEntity();
        when(motechDataService.retrieve("id", INSTANCE_ID))
                .thenReturn(new TestSample("Hello world", 99));

        EntityRecord record = instanceService.getEntityInstance(ENTITY_ID, INSTANCE_ID);

        assertNotNull(record);
        assertEquals(Long.valueOf(ENTITY_ID), record.getEntitySchemaId());
        assertEquals(Long.valueOf(INSTANCE_ID), record.getId());

        List<FieldRecord> fieldRecords = record.getFields();
        assertCommonFieldRecordFields(fieldRecords);
        assertEquals(asList("Hello world", 99, null, null),
                extract(fieldRecords, on(FieldRecord.class).getValue()));
    }

    @Test
    public void shouldReturnInstancesFromTrash() {
        mockSampleFields();
        mockEntity();
        QueryParams queryParams = new QueryParams(1, 10, null);
        when(trashService.getInstancesFromTrash(anyString(), eq(queryParams))).thenReturn(sampleCollection());

        List<EntityRecord> records = instanceService.getTrashRecords(ENTITY_ID, queryParams);
        verify(trashService).getInstancesFromTrash(anyString(), eq(queryParams));
        assertNotNull(records);
        assertEquals(records.size(), 1);

        //Make sure all fields that were in the instance are still available
        assertEquals(records.get(0).getFields().size(), 4);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void shouldThrowObjectNotFoundExceptionWhenNoInstanceFound() {
        mockDataService();
        mockSampleFields();
        mockEntity();

        instanceService.getEntityInstance(ENTITY_ID, INSTANCE_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowEntityNotFoundException() {
        instanceService.getEntityInstance(ENTITY_ID, INSTANCE_ID);
    }

    @Test
    public void shouldCreateNewInstances() throws ClassNotFoundException {
        testUpdateCreate(false);
    }

    @Test
    public void shouldUpdateObjectInstance() throws ClassNotFoundException {
        testUpdateCreate(true);
    }

    @Test
    public void shouldCountAllEntities() {
        mockSampleFields();
        mockDataService();
        mockEntity();

        when(motechDataService.count()).thenReturn(56L);

        assertEquals(56L, instanceService.countRecords(ENTITY_ID));
    }

    @Test
    public void shouldRetrieveInstancesBasedOnASingleReturnLookup() {
        mockSampleFields();
        mockEntity();
        mockLookups();
        mockLookupService();

        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put("strField", TestDataService.LOOKUP_1_EXPECTED_PARAM);

        List<EntityRecord> result = instanceService.getEntityRecordsFromLookup(ENTITY_ID, TestDataService.LOOKUP_1_NAME,
                lookupMap, queryParams());

        assertNotNull(result);
        assertEquals(1, result.size());

        EntityRecord entityRecord = result.get(0);
        assertEquals(Long.valueOf(ENTITY_ID), entityRecord.getEntitySchemaId());

        List<FieldRecord> fieldRecords = entityRecord.getFields();
        assertCommonFieldRecordFields(fieldRecords);
        assertEquals(asList("strField", 6, null, null),
                extract(fieldRecords, on(FieldRecord.class).getValue()));
    }

    @Test
    public void shouldRetrieveInstancesBasedOnAMultiReturnLookup() {
        mockSampleFields();
        mockEntity();
        mockLookups();
        mockLookupService();

        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put("strField", TestDataService.LOOKUP_2_EXPECTED_PARAM);

        List<EntityRecord> result = instanceService.getEntityRecordsFromLookup(ENTITY_ID, TestDataService.LOOKUP_2_NAME,
                lookupMap, queryParams());

        assertNotNull(result);
        assertEquals(2, result.size());

        EntityRecord entityRecord = result.get(0);
        assertEquals(Long.valueOf(ENTITY_ID), entityRecord.getEntitySchemaId());

        List<FieldRecord> fieldRecords = entityRecord.getFields();
        assertCommonFieldRecordFields(fieldRecords);
        assertEquals(asList("one", 1, null, null),
                extract(fieldRecords, on(FieldRecord.class).getValue()));

        entityRecord = result.get(1);
        assertEquals(Long.valueOf(ENTITY_ID), entityRecord.getEntitySchemaId());

        fieldRecords = entityRecord.getFields();
        assertCommonFieldRecordFields(fieldRecords);
        assertEquals(asList("two", 2, null, null),
                extract(fieldRecords, on(FieldRecord.class).getValue()));
    }

    @Test
    public void shouldHandleNullParamsForLookups() {
        mockSampleFields();
        mockEntity();
        mockLookups();
        mockLookupService();

        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put("dtField", null);

        List<EntityRecord> result = instanceService.getEntityRecordsFromLookup(ENTITY_ID,
                TestDataService.NULL_EXPECTING_LOOKUP_NAME, lookupMap, queryParams());

        assertNotNull(result);
        assertEquals(2, result.size());

        EntityRecord entityRecord = result.get(0);
        assertEquals(Long.valueOf(ENTITY_ID), entityRecord.getEntitySchemaId());

        List<FieldRecord> fieldRecords = entityRecord.getFields();
        assertCommonFieldRecordFields(fieldRecords);
        assertEquals(asList("three", 3, null, null),
                extract(fieldRecords, on(FieldRecord.class).getValue()));

        entityRecord = result.get(1);
        assertEquals(Long.valueOf(ENTITY_ID), entityRecord.getEntitySchemaId());

        fieldRecords = entityRecord.getFields();
        assertCommonFieldRecordFields(fieldRecords);
        assertEquals(asList("four", 4, null, null),
                extract(fieldRecords, on(FieldRecord.class).getValue()));
    }

    @Test
    public void shouldRevertInstanceFromTrash() {
        mockSampleFields();
        mockEntity();
        mockLookups();
        mockDataService();

        instanceService.revertInstanceFromTrash(ENTITY_ID, INSTANCE_ID);

        verify(motechDataService).revertFromTrash(INSTANCE_ID);
    }

    @Test
    public void shouldCountForLookup() {
        mockSampleFields();
        mockEntity();
        mockLookups();
        mockLookupService();

        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put("strField", TestDataService.LOOKUP_1_EXPECTED_PARAM);

        long count = instanceService.countRecordsByLookup(ENTITY_ID, TestDataService.LOOKUP_1_NAME, lookupMap);
        assertEquals(1L, count);

        lookupMap.put("strField", TestDataService.LOOKUP_2_EXPECTED_PARAM);

        count = instanceService.countRecordsByLookup(ENTITY_ID, TestDataService.LOOKUP_2_NAME, lookupMap);
        assertEquals(22L, count);

        lookupMap.clear();
        lookupMap.put("dtField", null);

        count = instanceService.countRecordsByLookup(ENTITY_ID, TestDataService.NULL_EXPECTING_LOOKUP_NAME, lookupMap);
        assertEquals(2, count);
    }

    @Test
    public void shouldUseCorrectClassLoaderWhenCreatingInstances() throws ClassNotFoundException {
        mockSampleFields();
        mockEntity();
        mockDataService();

        Bundle ddeBundle = mock(Bundle.class);
        Bundle entitiesBundle = mock(Bundle.class);
        when(bundleContext.getBundles()).thenReturn(new Bundle[]{ddeBundle, entitiesBundle});

        Dictionary<String, String> headers = new Hashtable<>();
        headers.put("Bundle-Name", "TestModule");
        when(entity.getModule()).thenReturn("TestModule");

        when(entitiesBundle.getSymbolicName()).thenReturn(Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
        when(ddeBundle.getHeaders()).thenReturn(headers);

        Class testClass = TestSample.class;
        when(entitiesBundle.loadClass(testClass.getName())).thenReturn(testClass);
        when(ddeBundle.loadClass(testClass.getName())).thenReturn(testClass);

        EntityRecord entityRecord = new EntityRecord(null, ENTITY_ID, Collections.<FieldRecord>emptyList());

        when(entity.isDDE()).thenReturn(true);
        instanceService.saveInstance(entityRecord);
        verify(ddeBundle).loadClass(TestSample.class.getName());

        when(entity.isDDE()).thenReturn(false);
        instanceService.saveInstance(entityRecord);
        verify(entitiesBundle).loadClass(TestSample.class.getName());

        verify(motechDataService, times(2)).create(any(TestSample.class));
    }

    private void testUpdateCreate(boolean edit) throws ClassNotFoundException {
        final DateTime dtValue = DateUtil.now();

        mockSampleFields();
        mockDataService();
        mockEntity();
        when(motechDataService.retrieve("id", INSTANCE_ID)).thenReturn(new TestSample());

        List<FieldRecord> fieldRecords = asList(
                FieldTestHelper.fieldRecord("strField", String.class.getName(), "", "this is a test"),
                FieldTestHelper.fieldRecord("intField", Integer.class.getName(), "", 16),
                FieldTestHelper.fieldRecord("timeField", Time.class.getName(), "", "10:17"),
                FieldTestHelper.fieldRecord("dtField", DateTime.class.getName(), "", dtValue)
        );

        Long id = (edit) ? INSTANCE_ID : null;
        EntityRecord record = new EntityRecord(id, ENTITY_ID, fieldRecords);

        MDSClassLoader.getInstance().loadClass(TestSample.class.getName());
        instanceService.saveInstance(record);

        ArgumentCaptor<TestSample> captor = ArgumentCaptor.forClass(TestSample.class);
        if (edit) {
            verify(motechDataService).update(captor.capture());
        } else {
            verify(motechDataService).create(captor.capture());
        }

        TestSample sample = captor.getValue();
        assertEquals("this is a test", sample.getStrField());
        assertEquals(Integer.valueOf(16), sample.getIntField());
        assertEquals(new Time(10, 17), sample.getTimeField());
        assertEquals(dtValue, sample.getDtField());
    }

    private void mockDataService() {
        when(bundleContext.getServiceReference(ClassName.getInterfaceName(TestSample.class.getName())))
                .thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(motechDataService);
    }

    private void mockSampleFields() {
        when(entityService.getEntityFields(ENTITY_ID)).thenReturn(asList(
                FieldTestHelper.fieldDto(1L, "strField", String.class.getName(), "String field", "Default"),
                FieldTestHelper.fieldDto(2L, "intField", Integer.class.getName(), "Integer field", 7),
                FieldTestHelper.fieldDto(3L, "dtField", DateTime.class.getName(), "DateTime field", null),
                FieldTestHelper.fieldDto(4L, "timeField", Time.class.getName(), "Time field", null)
        ));
    }

    private void mockEntity() {
        when(entityService.getEntity(ENTITY_ID)).thenReturn(entity);
    }

    private void mockLookups() {
        LookupDto lookup = new LookupDto(TestDataService.LOOKUP_1_NAME, true, true,
                asList(FieldTestHelper.lookupFieldDto(1L, "strField")), true, "singleObject");
        when(entityService.getLookupByName(ENTITY_ID, TestDataService.LOOKUP_1_NAME)).thenReturn(lookup);

        lookup = new LookupDto(TestDataService.LOOKUP_2_NAME, false, true,
                asList(FieldTestHelper.lookupFieldDto(1L, "strField")), false, "multiObject");
        when(entityService.getLookupByName(ENTITY_ID, TestDataService.LOOKUP_2_NAME)).thenReturn(lookup);

        lookup = new LookupDto(TestDataService.NULL_EXPECTING_LOOKUP_NAME, false, true,
                asList(FieldTestHelper.lookupFieldDto(3L, "dtField")), false, "nullParamExpected");
        when(entityService.getLookupByName(ENTITY_ID, TestDataService.NULL_EXPECTING_LOOKUP_NAME)).thenReturn(lookup);
    }

    private void mockLookupService() {
        when(bundleContext.getServiceReference(ClassName.getInterfaceName(TestSample.class.getName())))
                .thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(new TestDataService());
    }

    private QueryParams queryParams() {
        return new QueryParams(1, 5, new Order("strField", "desc"));
    }

    private void assertCommonFieldRecordFields(List<FieldRecord> fieldRecords) {
        assertNotNull(fieldRecords);
        assertEquals(4, fieldRecords.size());
        assertEquals(asList("String field", "Integer field", "DateTime field", "Time field"),
                extract(fieldRecords, on(FieldRecord.class).getDisplayName()));
        assertEquals(asList("strField", "intField", "dtField", "timeField"),
                extract(fieldRecords, on(FieldRecord.class).getName()));
        assertEquals(asList(String.class.getName(), Integer.class.getName(),
                        DateTime.class.getName(), Time.class.getName()),
                extract(fieldRecords, on(FieldRecord.class).getType().getTypeClass()));
    }

    private Collection sampleCollection() {
        return Arrays.asList(new TestSample("a", 1));
    }

    public static class TestSample {

        private Long id = 4L;

        private String strField = "Default";
        private Integer intField = 7;
        private DateTime dtField;
        private Time timeField;

        public TestSample() {
        }

        public TestSample(String strField, Integer intField) {
            this.strField = strField;
            this.intField = intField;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getStrField() {
            return strField;
        }

        public void setStrField(String strField) {
            this.strField = strField;
        }

        public Integer getIntField() {
            return intField;
        }

        public void setIntField(Integer intField) {
            this.intField = intField;
        }

        public DateTime getDtField() {
            return dtField;
        }

        public void setDtField(DateTime dtField) {
            this.dtField = dtField;
        }

        public Time getTimeField() {
            return timeField;
        }

        public void setTimeField(Time timeField) {
            this.timeField = timeField;
        }
    }

    public static class TestDataService extends DefaultMotechDataService<TestSample> {

        public static final String LOOKUP_1_NAME = "Single Object";
        public static final String LOOKUP_2_NAME = "MultiObject";
        public static final String NULL_EXPECTING_LOOKUP_NAME = "nullParamExpected";

        public static final String LOOKUP_1_EXPECTED_PARAM = "strFieldSingle";
        public static final String LOOKUP_2_EXPECTED_PARAM = "strFieldMulti";


        public TestSample singleObject(String strField, QueryParams queryParams) {
            assertEquals(strField, LOOKUP_1_EXPECTED_PARAM);
            assertEquals(Integer.valueOf(1), queryParams.getPage());
            assertEquals(Integer.valueOf(5), queryParams.getPageSize());
            assertEquals("strField descending", queryParams.getOrder().toString());

            return new TestSample("strField", 6);
        }

        public long countSingleObject(String strField) {
            assertEquals(LOOKUP_1_EXPECTED_PARAM, strField);
            return 1;
        }

        public List<TestSample> multiObject(String strField, QueryParams queryParams) {
            assertEquals(LOOKUP_2_EXPECTED_PARAM, strField);
            return asList(new TestSample("one", 1), new TestSample("two", 2));
        }

        public List<TestSample> nullParamExpected(DateTime dtField, QueryParams queryParams) {
            assertNull(dtField);
            return asList(new TestSample("three", 3), new TestSample("four", 4));
        }

        public long countNullParamExpected(DateTime dtField) {
            assertNull(dtField);
            return 2;
        }

        public long countMultiObject(String strField) {
            assertEquals(LOOKUP_2_EXPECTED_PARAM, strField);
            return 22;
        }

        public TestSample findTrashInstanceById(Object instanceId, Object entityId) {
            return new TestSample();
        }

        public void revertFromTrash(Object newInstance, Object trash) {
            assertEquals(newInstance.getClass().getDeclaredMethods(), trash.getClass().getDeclaredMethods());
        }

        @Override
        public Class<TestSample> getClassType() {
            return TestSample.class;
        }
    }
}
