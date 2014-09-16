package org.motechproject.mds.rest;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.ex.rest.RestBadBodyFormatException;
import org.motechproject.mds.ex.rest.RestLookupExecutionForbbidenException;
import org.motechproject.mds.ex.rest.RestLookupNotFoundException;
import org.motechproject.mds.ex.rest.RestOperationNotSupportedException;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.testutil.FieldTestHelper;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.util.Order;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MdsRestFacadeTest {

    private static final String FORBIDDEN_LOOKUP_NAME = "forbiddenLookup";
    private static final String SUPPORTED_LOOKUP_NAME = "supportedLookup";
    private static final String STR_FIELD = "strField";
    private static final String INT_FIELD = "intField";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AllEntities allEntities;

    @Mock
    private Entity entity;

    @Mock
    private RestFacadeTestService dataService;

    @Mock
    private RestOptions restOptions;

    @Mock
    private RestOptionsDto restOptionsDto;

    @InjectMocks
    private MdsRestFacadeImpl<Record> mdsRestFacade = new MdsRestFacadeImpl<>();

    @Before
    public void setUp() {
        when(dataService.getClassType()).thenReturn(Record.class);
        when(allEntities.retrieveByClassName(Record.class.getName())).thenReturn(entity);
        when(entity.getRestOptions()).thenReturn(restOptions);
        when(restOptions.toDto()).thenReturn(restOptionsDto);

        // set up lookups
        FieldDto strField = FieldTestHelper.fieldDto(1L, STR_FIELD, String.class.getName(), STR_FIELD, null);
        FieldDto intField = FieldTestHelper.fieldDto(2L, INT_FIELD, Integer.class.getName(), INT_FIELD, null);
        when(entity.getFieldDtos()).thenReturn(asList(intField, strField));

        LookupDto forbiddenLookup = new LookupDto(FORBIDDEN_LOOKUP_NAME, true, false,
                asList(FieldTestHelper.lookupFieldDto(1L, STR_FIELD), FieldTestHelper.lookupFieldDto(2L, INT_FIELD)),
                true);
        LookupDto supportedLookup = new LookupDto(SUPPORTED_LOOKUP_NAME, true, true,
                asList(FieldTestHelper.lookupFieldDto(1L, STR_FIELD), FieldTestHelper.lookupFieldDto(2L, INT_FIELD)),
                true);
        when(entity.getLookupDtos()).thenReturn(asList(forbiddenLookup, supportedLookup));

        // do the initialization, normally called by Spring as @PostConstruct
        mdsRestFacade.init();
    }

    // regular verifications

    @Test
    public void shouldDoReadOperations() {
        setUpCrudAccess(false, true, false, false);
        Record record = mock(Record.class);
        when(dataService.retrieveAll(any(QueryParams.class)))
                .thenReturn(asList(record));

        when(dataService.findById(1l))
                .thenReturn(record);

        List<Record> result = mdsRestFacade.get(new QueryParams(5, 20,
                new Order("value", Order.Direction.DESC)));
        Record recResult = mdsRestFacade.get(1l);

        assertEquals(asList(record), result);
        assertEquals(record, recResult);

        ArgumentCaptor<QueryParams> captor = ArgumentCaptor.forClass(QueryParams.class);
        verify(dataService).retrieveAll(captor.capture());

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(dataService).findById(longCaptor.capture());

        assertNotNull(captor.getValue());
        assertEquals(Integer.valueOf(5), captor.getValue().getPage());
        assertEquals(Integer.valueOf(20), captor.getValue().getPageSize());
        assertEquals(Long.valueOf(1), longCaptor.getValue());
        assertNotNull(captor.getValue().getOrder());
        assertEquals("value", captor.getValue().getOrder().getField());
        assertEquals(Order.Direction.DESC, captor.getValue().getOrder().getDirection());
    }

    @Test
    public void shouldDoCreateOperation() throws IOException {
        setUpCrudAccess(true, false, false, false);

        Record record = testRecord();

        try (InputStream recordAsStream = toInputStream(record)) {
            mdsRestFacade.create(recordAsStream);
        }

        ArgumentCaptor<Record> captor = ArgumentCaptor.forClass(Record.class);
        verify(dataService).create(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals("restTest", captor.getValue().getValue());
    }

    @Test
    public void shouldDoUpdateOperation() throws IOException {
        setUpCrudAccess(false, false, true, false);

        Record record = testRecord();

        try (InputStream recordAsStream = toInputStream(record)) {
            mdsRestFacade.update(recordAsStream);
        }

        ArgumentCaptor<Record> captor = ArgumentCaptor.forClass(Record.class);
        verify(dataService).updateFromTransient(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals("restTest", captor.getValue().getValue());
    }

    @Test
    public void shouldDoDeleteOperation() throws IOException {
        setUpCrudAccess(false, false, false, true);

        mdsRestFacade.delete(14L);

        verify(dataService).delete("id", 14L);
    }

    @Test
    public void shouldExecuteLookups() {
        Map<String, String> lookupMap = asLookupMap(null, "44");
        QueryParams queryParams = mock(QueryParams.class);
        Record record = testRecord();
        when(dataService.supportedLookup(null, 44, queryParams))
                .thenReturn(asList(record));

        List<Record> result = (List<Record>) mdsRestFacade.executeLookup(SUPPORTED_LOOKUP_NAME, lookupMap, queryParams);

        assertEquals(asList(record), result);
        verify(dataService).supportedLookup(null, 44, queryParams);
    }

    // bad input exceptions verifications

    @Test(expected = RestBadBodyFormatException.class)
    public void shouldThrowBadBodyFormatExceptionForCreatesWithBadBody() throws IOException {
        setUpCrudAccess(true, false, false, false);
        try (InputStream badBodyInput = IOUtils.toInputStream("This is not a record object")) {
            mdsRestFacade.create(badBodyInput);
        }
    }

    @Test(expected = RestBadBodyFormatException.class)
    public void shouldThrowBadBodyFormatExceptionForUpdatesWithBadBody() throws IOException {
        setUpCrudAccess(false, false, true, false);
        try (InputStream badBodyInput = IOUtils.toInputStream("This is not a record object")) {
            mdsRestFacade.update(badBodyInput);
        }
    }

    // Lookup exceptions

    @Test(expected = RestLookupNotFoundException.class)
    public void shouldThrowLookupNotFoundException() {
        mdsRestFacade.executeLookup("nonExistent", new HashMap<String, String>(), null);
    }

    @Test(expected = RestLookupExecutionForbbidenException.class)
    public void shouldThrowLookupForbiddenException() {
        mdsRestFacade.executeLookup(FORBIDDEN_LOOKUP_NAME, asLookupMap("something", "55"), null);
    }

    // Unsupported exceptions verification

    @Test(expected = RestOperationNotSupportedException.class)
    public void shouldThrowExceptionForUnsupportedCreate() throws IOException {
        setUpCrudAccess(false, true, true, true);
        mdsRestFacade.create(mock(InputStream.class));
    }

    @Test(expected = RestOperationNotSupportedException.class)
    public void shouldThrowExceptionForUnsupportedRead() {
        setUpCrudAccess(true, false, true, true);
        mdsRestFacade.get(new QueryParams(1, 10));
    }

    @Test(expected = RestOperationNotSupportedException.class)
    public void shouldThrowExceptionForUnsupportedUpdate() {
        setUpCrudAccess(true, true, false, true);
        mdsRestFacade.update(null);
    }

    @Test(expected = RestOperationNotSupportedException.class)
    public void shouldThrowExceptionForUnsupportedDelete() {
        setUpCrudAccess(true, true, true, false);
        mdsRestFacade.delete(1L);
    }

    private void setUpCrudAccess(boolean allowCreate, boolean allowRead,
                                 boolean allowUpdate, boolean allowDelete) {
        when(restOptionsDto.isCreate()).thenReturn(allowCreate);
        when(restOptionsDto.isRead()).thenReturn(allowRead);
        when(restOptionsDto.isUpdate()).thenReturn(allowUpdate);
        when(restOptionsDto.isDelete()).thenReturn(allowDelete);
    }

    private Record testRecord() {
        Record record = new Record();
        record.setValue("restTest");
        return record;
    }

    private Map<String, String> asLookupMap(String strField, String intField) {
        Map<String, String> map = new HashMap<>();

        if (strField != null) {
            map.put(STR_FIELD, strField);
        }

        map.put(INT_FIELD, intField);

        // check that additional fields in the map don't cause issues
        map.put("lookup", "lookupName");
        map.put("page", "1");

        return map;
    }

    private InputStream toInputStream(Record record) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            objectMapper.writeValue(baos, record);
            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

    private interface RestFacadeTestService extends MotechDataService<Record> {
        List<Record> forbiddenLookup(String strField, Integer intField);
        List<Record> supportedLookup(String strField, Integer intField, QueryParams queryParams);
    }
}
