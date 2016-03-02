package org.motechproject.mds.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.LookupFieldType;
import org.motechproject.mds.exception.lookup.SingleResultFromLookupExpectedException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.DefaultMotechDataService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.util.Order;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsLookupServiceTest {

    private static final String ENTITY_CLASS_NAME = Record.class.getName();
    private static final long ENTITY_ID = 1;

    private static final String FIRST_LOOKUP_NAME = "firstLookup";
    private static final String SECOND_LOOKUP_NAME = "secondLookup";

    private static final String STR_PARAM = "strParam";
    private static final String INT_PARAM = "intParam";
    private static final String STR_PARAM_VAL = "queryValue";
    private static final Integer INT_PARAM_VAL = 16;
    private static final long STR_FIELD_ID = 11;
    private static final long INT_FIELD_ID = 22;

    private static final String SINGLE_LOOKUP_VAL = "va1";
    private static final String MULTI_LOOKUP_VAL_1 = "val2";
    private static final String MULTI_LOOKUP_VAL_2 = "val3";

    private static final long FIRST_LOOKUP_COUNT = 1;
    private static final long SECOND_LOOKUP_COUNT = 2;
    private static final long TOTAL_COUNT = 3;

    private static final QueryParams QUERY_PARAMS = new QueryParams(2, 11, new Order("strPara,", Order.Direction.DESC));

    @InjectMocks
    private MDSLookupService mdsLookupService = new MdsLookupServiceImpl();

    private TestDataService dataService = new TestDataService();

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference serviceReference;

    @Mock
    private EntityService entityService;

    @Before
    public void setUp() {
        MotechClassPool.registerServiceInterface(Record.class.getName(), TestDataService.class.getName());

        EntityDto entity = new EntityDto(ENTITY_ID, ENTITY_CLASS_NAME);

        FieldDto strField = FieldTestHelper.fieldDto(STR_FIELD_ID, STR_PARAM, String.class.getName(), "strDisp", null);
        FieldDto intField = FieldTestHelper.fieldDto(INT_FIELD_ID, INT_PARAM, Integer.class.getName(), "strDisp", null);

        LookupFieldDto strLookupField = new LookupFieldDto(STR_FIELD_ID, STR_PARAM, LookupFieldType.VALUE);
        LookupFieldDto intLookupField = new LookupFieldDto(INT_FIELD_ID, INT_PARAM, LookupFieldType.VALUE);

        LookupDto firstLookup = new LookupDto(FIRST_LOOKUP_NAME, true, false,
                asList(strLookupField, intLookupField), false);
        LookupDto secondLookup = new LookupDto(SECOND_LOOKUP_NAME, false, false,
                asList(strLookupField, intLookupField), false);

        when(entityService.getEntityByClassName(ENTITY_CLASS_NAME)).thenReturn(entity);
        when(entityService.getEntityFields(ENTITY_ID)).thenReturn(asList(intField, strField));
        when(entityService.getLookupByName(ENTITY_ID, FIRST_LOOKUP_NAME)).thenReturn(firstLookup);
        when(entityService.getLookupByName(ENTITY_ID, SECOND_LOOKUP_NAME)).thenReturn(secondLookup);

        Map<String, FieldDto> mapping = new HashMap<>();
        mapping.put(STR_PARAM, strField);
        mapping.put(INT_PARAM, intField);
        when(entityService.getLookupFieldsMapping(ENTITY_ID, FIRST_LOOKUP_NAME)).thenReturn(mapping);
        when(entityService.getLookupFieldsMapping(ENTITY_ID, SECOND_LOOKUP_NAME)).thenReturn(mapping);

        when(bundleContext.getServiceReference(TestDataService.class.getName())).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(dataService);
    }

    @After
    public void tearDown() {
        MotechClassPool.clearEnhancedData();
    }

    @Test
    public void shouldExecuteSingleReturnLookups() {
        Map<String, Object> lookupMap = lookupMap();

        Record recordByClass = mdsLookupService.findOne(Record.class, FIRST_LOOKUP_NAME, lookupMap);
        Record recordByClassName = mdsLookupService.findOne(Record.class.getName(), FIRST_LOOKUP_NAME, lookupMap);

        assertNotNull(recordByClass);
        assertEquals(SINGLE_LOOKUP_VAL, recordByClass.getValue());
        assertNotNull(recordByClassName);
        assertEquals(SINGLE_LOOKUP_VAL, recordByClassName.getValue());
    }

    @Test
    public void shouldExecuteMultiReturnLookups() {
        Map<String, Object> lookupMap = lookupMap();

        List<Record> recordsByClass = mdsLookupService.findMany(Record.class, SECOND_LOOKUP_NAME, lookupMap);
        List<Record> recordsByClassName = mdsLookupService.findMany(Record.class.getName(), SECOND_LOOKUP_NAME, lookupMap);

        assertNotNull(recordsByClass);
        assertEquals(asList(MULTI_LOOKUP_VAL_1, MULTI_LOOKUP_VAL_2),
                extract(recordsByClass, on(Record.class).getValue()));
        assertNotNull(recordsByClassName);
        assertEquals(asList(MULTI_LOOKUP_VAL_1, MULTI_LOOKUP_VAL_2),
                extract(recordsByClassName, on(Record.class).getValue()));
    }

    @Test
    public void shouldExecuteMultiReturnWithQueryParamsLookups() {
        Map<String, Object> lookupMap = lookupMap();

        List<Record> recordsByClass = mdsLookupService.findMany(Record.class, SECOND_LOOKUP_NAME, lookupMap,
                QUERY_PARAMS);
        List<Record> recordsByClassName = mdsLookupService.findMany(Record.class.getName(), SECOND_LOOKUP_NAME,
                lookupMap, QUERY_PARAMS);

        assertNotNull(recordsByClass);
        assertEquals(asList(MULTI_LOOKUP_VAL_2, MULTI_LOOKUP_VAL_1),
                extract(recordsByClass, on(Record.class).getValue()));
        assertNotNull(recordsByClassName);
        assertEquals(asList(MULTI_LOOKUP_VAL_2, MULTI_LOOKUP_VAL_1),
                extract(recordsByClassName, on(Record.class).getValue()));
    }

    @Test
    public void shouldRetrieveAllInstances() {
        List<Record> recordsByClass = mdsLookupService.retrieveAll(Record.class);
        List<Record> recordsByClassName = mdsLookupService.retrieveAll(Record.class.getName());

        assertNotNull(recordsByClass);
        assertEquals(asList(MULTI_LOOKUP_VAL_1, MULTI_LOOKUP_VAL_2, SINGLE_LOOKUP_VAL),
                extract(recordsByClass, on(Record.class).getValue()));
        assertNotNull(recordsByClassName);
        assertEquals(asList(MULTI_LOOKUP_VAL_1, MULTI_LOOKUP_VAL_2, SINGLE_LOOKUP_VAL),
                extract(recordsByClassName, on(Record.class).getValue()));
    }

    @Test
    public void shouldRetrieveAllInstancesWithQueryParams() {
        List<Record> recordsByClass = mdsLookupService.retrieveAll(Record.class, QUERY_PARAMS);
        List<Record> recordsByClassName = mdsLookupService.retrieveAll(Record.class.getName(), QUERY_PARAMS);

        assertNotNull(recordsByClass);
        assertEquals(asList(SINGLE_LOOKUP_VAL, MULTI_LOOKUP_VAL_2, MULTI_LOOKUP_VAL_1),
                extract(recordsByClass, on(Record.class).getValue()));
        assertNotNull(recordsByClassName);
        assertEquals(asList(SINGLE_LOOKUP_VAL, MULTI_LOOKUP_VAL_2, MULTI_LOOKUP_VAL_1),
                extract(recordsByClassName, on(Record.class).getValue()));
    }

    @Test
    public void shouldExecuteCounts() {
        Map<String, Object> lookupMap = lookupMap();

        assertEquals(FIRST_LOOKUP_COUNT, mdsLookupService.count(Record.class, FIRST_LOOKUP_NAME, lookupMap));
        assertEquals(FIRST_LOOKUP_COUNT, mdsLookupService.count(Record.class.getName(), FIRST_LOOKUP_NAME, lookupMap));

        assertEquals(SECOND_LOOKUP_COUNT, mdsLookupService.count(Record.class, SECOND_LOOKUP_NAME, lookupMap));
        assertEquals(SECOND_LOOKUP_COUNT, mdsLookupService.count(Record.class.getName(), SECOND_LOOKUP_NAME, lookupMap));

        assertEquals(TOTAL_COUNT, mdsLookupService.countAll(Record.class));
        assertEquals(TOTAL_COUNT, mdsLookupService.countAll(Record.class.getName()));
    }

    @Test(expected = SingleResultFromLookupExpectedException.class)
    public void shouldThrowExceptionWhenFindOneCalledForMultiReturnLookup() {
        Map<String, Object> lookupMap = lookupMap();
        mdsLookupService.findOne(Record.class, SECOND_LOOKUP_NAME, lookupMap);
    }

    private Map<String, Object> lookupMap() {
        Map<String, Object> lookupMap = new HashMap<>();
        lookupMap.put(STR_PARAM, STR_PARAM_VAL);
        lookupMap.put(INT_PARAM, INT_PARAM_VAL);
        return lookupMap;
    }

    public static class TestDataService extends DefaultMotechDataService<Record> {

        public Record firstLookup(String strParam, Integer intParam) {
            assertParams(strParam, intParam);

            Record result = new Record();
            result.setValue(SINGLE_LOOKUP_VAL);

            return result;
        }

        public List<Record> secondLookup(String strParam, Integer intParam) {
            return secondLookup(strParam, intParam, null);
        }

        public List<Record> secondLookup(String strParam, Integer intParam, QueryParams queryParams) {
            assertParams(strParam, intParam);
            if (queryParams != null) {
                assertQueryParams(queryParams);
            }

            Record record1 = new Record();
            record1.setValue(MULTI_LOOKUP_VAL_1);

            Record record2 = new Record();
            record2.setValue(MULTI_LOOKUP_VAL_2);

            // reverse if query params passed
            return (queryParams != null) ? asList(record2, record1) : asList(record1, record2);
        }

        @Override
        public List<Record> retrieveAll() {
            return retrieveAll((QueryParams) null);
        }

        @Override
        public List<Record> retrieveAll(QueryParams queryParams) {
            if (queryParams != null) {
                assertQueryParams(queryParams);
            }

            Record record1 = new Record();
            record1.setValue(MULTI_LOOKUP_VAL_1);

            Record record2 = new Record();
            record2.setValue(MULTI_LOOKUP_VAL_2);

            Record record3 = new Record();
            record3.setValue(SINGLE_LOOKUP_VAL);

            List<Record> results = new ArrayList<>(asList(record1, record2, record3));
            // reverse if query params passed
            if (queryParams != null) {
                Collections.reverse(results);
            }

            return results;
        }

        public long countFirstLookup(String strParam, Integer intParam) {
            assertParams(strParam, intParam);
            return FIRST_LOOKUP_COUNT;
        }

        public long countSecondLookup(String strParam, Integer intParam) {
            assertParams(strParam, intParam);
            return SECOND_LOOKUP_COUNT;
        }

        @Override
        public long count() {
            return TOTAL_COUNT;
        }

        @Override
        public Class<Record> getClassType() {
            return Record.class;
        }

        private void assertParams(String strParam, Integer intParam) {
            assertEquals(STR_PARAM_VAL, strParam);
            assertEquals(INT_PARAM_VAL, intParam);
        }

        private void assertQueryParams(QueryParams queryParams) {
            assertEquals(QUERY_PARAMS, queryParams);
        }
    }
}
