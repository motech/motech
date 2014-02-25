package org.motechproject.mds.service.impl.internal;

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
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.ObjectNotFoundException;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.InstanceService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.DefaultMotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldDto;
import static org.motechproject.mds.testutil.FieldTestHelper.fieldRecord;

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

    @Before
    public void setUp() {
        when(entity.getClassName()).thenReturn(TestSample.class.getName());
        when(entity.getId()).thenReturn(ENTITY_ID);
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

        Map<String, String> lookupMap = new HashMap<>();
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

        Map<String, String> lookupMap = new HashMap<>();
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
    public void shouldCountForLookup() {
        mockSampleFields();
        mockEntity();
        mockLookups();
        mockLookupService();

        Map<String, String> lookupMap = new HashMap<>();
        lookupMap.put("strField", TestDataService.LOOKUP_1_EXPECTED_PARAM);

        long count = instanceService.countRecordsByLookup(ENTITY_ID, TestDataService.LOOKUP_1_NAME, lookupMap);
        assertEquals(1L, count);

        lookupMap.put("strField", TestDataService.LOOKUP_2_EXPECTED_PARAM);

        count = instanceService.countRecordsByLookup(ENTITY_ID, TestDataService.LOOKUP_2_NAME, lookupMap);
        assertEquals(22L, count);
    }

    private void testUpdateCreate(boolean edit) throws ClassNotFoundException {
        final DateTime dtValue = DateUtil.now();

        mockSampleFields();
        mockDataService();
        mockEntity();
        when(motechDataService.retrieve("id", INSTANCE_ID)).thenReturn(new TestSample());

        List<FieldRecord> fieldRecords = asList(
                fieldRecord("strField", String.class.getName(), "", "this is a test"),
                fieldRecord("intField", Integer.class.getName(), "", 16),
                fieldRecord("timeField", Time.class.getName(), "", "10:17"),
                fieldRecord("dtField", DateTime.class.getName(), "", dtValue)
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
            fieldDto(1L, "strField", String.class.getName(), "String field", "Default"),
            fieldDto(2L, "intField", Integer.class.getName(), "Integer field", 7),
                fieldDto(3L, "dtField", DateTime.class.getName(), "DateTime field", null),
                fieldDto(4L, "timeField", Time.class.getName(), "Time field", null)
        ));
    }

    private void mockEntity() {
        when(entityService.getEntity(ENTITY_ID)).thenReturn(entity);
    }

    private void mockLookups() {
        LookupDto lookup = new LookupDto(TestDataService.LOOKUP_1_NAME, true, true, asList(1L), asList("strField"), false);
        when(entityService.getLookupByName(ENTITY_ID, TestDataService.LOOKUP_1_NAME)).thenReturn(lookup);
        lookup = new LookupDto(TestDataService.LOOKUP_2_NAME, false, true, asList(1L), asList("strField"), false);
        when(entityService.getLookupByName(ENTITY_ID, TestDataService.LOOKUP_2_NAME)).thenReturn(lookup);
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
            assertEquals(strField, LOOKUP_1_EXPECTED_PARAM);
            return 1;
        }

        public List<TestSample> multiObject(String strField, QueryParams queryParams) {
            assertEquals(strField, LOOKUP_2_EXPECTED_PARAM);
            return asList(new TestSample("one", 1), new TestSample("two", 2));
        }

        public long countMultiObject(String strField) {
            assertEquals(strField, LOOKUP_2_EXPECTED_PARAM);
            return 22;
        }
    }
}
