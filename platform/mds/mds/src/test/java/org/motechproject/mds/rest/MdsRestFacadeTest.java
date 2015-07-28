package org.motechproject.mds.rest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.RestOptions;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.rest.RestBadBodyFormatException;
import org.motechproject.mds.ex.rest.RestLookupExecutionForbbidenException;
import org.motechproject.mds.ex.rest.RestLookupNotFoundException;
import org.motechproject.mds.ex.rest.RestNoLookupResultException;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
    private static final String VALUE_FIELD = "value";
    private static final String DATE_FIELD = "date";
    private static final String BLOB_FIELD = "blob";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AllEntities allEntities;

    @Mock
    private Entity entity;

    @Mock
    private Field valueField;

    @Mock
    private RestFacadeTestService dataService;

    @Mock
    private RestOptions restOptions;

    @Mock
    private RestOptionsDto restOptionsDto;

    @InjectMocks
    private MdsRestFacadeImpl<Record> mdsRestFacade = new MdsRestFacadeImpl<>();

    private Record recordOne;

    private Byte[] blobFieldValue = ArrayUtils.toObject("TestBlobValue".getBytes());

    private byte[] encodedBlobField = Base64.encodeBase64(ArrayUtils.toPrimitive(blobFieldValue));

    @Before
    public void setUp() {
        when(dataService.getClassType()).thenReturn(Record.class);
        when(allEntities.retrieveByClassName(Record.class.getName())).thenReturn(entity);
        when(entity.getRestOptions()).thenReturn(restOptions);
        when(restOptions.toDto()).thenReturn(restOptionsDto);

        // set up rest fields
        FieldDto valueField = FieldTestHelper.fieldDto(3L, VALUE_FIELD, String.class.getName(), VALUE_FIELD, null);
        FieldDto dateField = FieldTestHelper.fieldDto(4L, DATE_FIELD, Date.class.getName(), DATE_FIELD, null);
        FieldDto blobField = FieldTestHelper.fieldDto(5L, BLOB_FIELD, Byte[].class.getName(), BLOB_FIELD, null);
        blobField.setType(new TypeDto("mds.field.blob", StringUtils.EMPTY, BLOB_FIELD, Byte[].class.getName()));
        when(restOptionsDto.getFieldNames()).thenReturn(Arrays.asList(VALUE_FIELD, DATE_FIELD, BLOB_FIELD));

        // set up lookups
        FieldDto strField = FieldTestHelper.fieldDto(1L, STR_FIELD, String.class.getName(), STR_FIELD, null);
        FieldDto intField = FieldTestHelper.fieldDto(2L, INT_FIELD, Integer.class.getName(), INT_FIELD, null);
        when(entity.getFieldDtos()).thenReturn(asList(intField, strField, valueField, dateField, blobField));
        when(entity.getField(STR_FIELD)).thenReturn(FieldTestHelper.field(1l, STR_FIELD, String.class));
        when(entity.getField(INT_FIELD)).thenReturn(FieldTestHelper.field(2l, INT_FIELD, Integer.class));

        LookupDto forbiddenLookup = new LookupDto(FORBIDDEN_LOOKUP_NAME, true, false,
                asList(FieldTestHelper.lookupFieldDto(1L, STR_FIELD), FieldTestHelper.lookupFieldDto(2L, INT_FIELD)),
                true);
        LookupDto supportedLookup = new LookupDto(SUPPORTED_LOOKUP_NAME, true, true,
                asList(FieldTestHelper.lookupFieldDto(1L, STR_FIELD), FieldTestHelper.lookupFieldDto(2L, INT_FIELD)),
                true);
        when(entity.getLookupDtos()).thenReturn(asList(forbiddenLookup, supportedLookup));

        //set up record
        recordOne = testRecord();

        //set up data service
        when(dataService.retrieveAll(any(QueryParams.class))).thenReturn(asList(recordOne));
        when(dataService.retrieveAll()).thenReturn(asList(recordOne));
        when(dataService.findById(1l)).thenReturn(recordOne);
        when(dataService.create(recordOne)).thenReturn(recordOne);
        when(dataService.getDetachedField(recordOne, BLOB_FIELD))
                .thenReturn(blobFieldValue);

        // do the initialization, normally called by Spring as @PostConstruct
        mdsRestFacade.init();
    }

    // regular verifications

    @Test
    public void shouldGetByQueryParamsWithoutBlobField() {
        setUpCrudAccess(false, true, false, false);
        QueryParams queryParams = new QueryParams(5, 20, new Order("value", Order.Direction.DESC));

        List<RestProjection> result = mdsRestFacade.get(queryParams, false);

        verify(dataService).retrieveAll(queryParams);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).size());
        assertEquals(recordOne.getValue(), result.get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.get(0).get(DATE_FIELD));
        assertNull(result.get(0).get(BLOB_FIELD));
    }

    @Test
    public void shouldGetByIdWithoutBlobField() {

        setUpCrudAccess(false, true, false, false);

        RestProjection recResult = mdsRestFacade.get(1l, false);

        assertEquals(3, recResult.size());
        assertEquals(recordOne.getValue(), recResult.get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), recResult.get(DATE_FIELD));
        assertNull(recResult.get(BLOB_FIELD));

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(dataService).findById(longCaptor.capture());

        assertEquals(Long.valueOf(1), longCaptor.getValue());
    }

    @Test
    public void shouldGetByQueryParamsWithBlobField() {
        setUpCrudAccess(false, true, false, false);

        QueryParams queryParams = new QueryParams(5, 20, new Order("value", Order.Direction.DESC));

        List<RestProjection> result = mdsRestFacade.get(queryParams, true);

        verify(dataService).retrieveAll(queryParams);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).size());
        assertEquals(recordOne.getValue(), result.get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.get(0).get(DATE_FIELD));
        assertArrayEquals(encodedBlobField, (byte[]) result.get(0).get(BLOB_FIELD));
    }

    @Test
    public void shouldGetByIdWithBlobField() {

        setUpCrudAccess(false, true, false, false);

        RestProjection result = mdsRestFacade.get(1l, true);

        assertEquals(3, result.size());
        assertEquals(recordOne.getValue(), result.get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.get(DATE_FIELD));
        assertArrayEquals(encodedBlobField, (byte[]) result.get(BLOB_FIELD));

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(dataService).findById(longCaptor.capture());

        assertEquals(Long.valueOf(1), longCaptor.getValue());
    }

    @Test
    public void shouldDoCreateOperation() throws IOException {
        setUpCrudAccess(true, false, false, false);
        recordOne.setBlob(blobFieldValue);

        try (InputStream recordAsStream = toInputStream(recordOne)) {
            mdsRestFacade.create(recordAsStream);
        }

        ArgumentCaptor<Record> captor = ArgumentCaptor.forClass(Record.class);
        verify(dataService).create(captor.capture());
        assertNotNull(captor.getValue());
        assertEquals("restTest", captor.getValue().getValue());
        assertArrayEquals(blobFieldValue, captor.getValue().getBlob());
        assertNull(captor.getValue().getDateIgnoredByRest());
    }

    @Test
    public void shouldDoUpdateOperation() throws IOException {
        setUpCrudAccess(false, false, true, false);

        try (InputStream recordAsStream = toInputStream(recordOne)) {
            mdsRestFacade.update(recordAsStream);
        }

        ArgumentCaptor<Record> captor = ArgumentCaptor.forClass(Record.class);
        ArgumentCaptor<Set> fieldsToCopyCaptor = ArgumentCaptor.forClass(Set.class);
        verify(dataService).updateFromTransient(captor.capture(), fieldsToCopyCaptor.capture());

        assertNotNull(captor.getValue());
        assertNotNull(fieldsToCopyCaptor.getValue());
        assertEquals("restTest", captor.getValue().getValue());
        assertEquals(3, fieldsToCopyCaptor.getValue().size());
        assertTrue(fieldsToCopyCaptor.getValue().contains("value"));
        assertTrue(fieldsToCopyCaptor.getValue().contains("date"));
        assertFalse(fieldsToCopyCaptor.getValue().contains("dateIgnoredByRest"));
    }

    @Test
    public void shouldDoDeleteOperation() throws IOException {
        setUpCrudAccess(false, false, false, true);

        mdsRestFacade.delete(14L);

        verify(dataService).deleteById(14L);
    }

    @Test
    public void shouldExecuteLookupWithoutBlobField() {

        Map<String, String> lookupMap = asLookupMap(null, "44");
        QueryParams queryParams = mock(QueryParams.class);

        when(dataService.supportedLookup(null, 44, queryParams))
                .thenReturn(asList(recordOne));

        List<RestProjection> result = (List<RestProjection>) mdsRestFacade.executeLookup(SUPPORTED_LOOKUP_NAME, lookupMap, queryParams, false);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).size());
        assertEquals(recordOne.getValue(), result.get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.get(0).get(DATE_FIELD));
        assertNull(result.get(0).get(BLOB_FIELD));

        verify(dataService).supportedLookup(null, 44, queryParams);
    }

    @Test
    public void shouldExecuteLookupWithBlobField() {

        Map<String, String> lookupMap = asLookupMap(null, "44");
        QueryParams queryParams = mock(QueryParams.class);

        when(dataService.supportedLookup(null, 44, queryParams))
                .thenReturn(asList(recordOne));

        List<RestProjection> result = (List<RestProjection>) mdsRestFacade.executeLookup(SUPPORTED_LOOKUP_NAME, lookupMap, queryParams, true);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).size());
        assertEquals(recordOne.getValue(), result.get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.get(0).get(DATE_FIELD));
        assertArrayEquals((byte[]) result.get(0).get(BLOB_FIELD), encodedBlobField);

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
        mdsRestFacade.executeLookup("nonExistent", new HashMap<String, String>(), null, false);
    }

    @Test(expected = RestLookupExecutionForbbidenException.class)
    public void shouldThrowLookupForbiddenException() {
        mdsRestFacade.executeLookup(FORBIDDEN_LOOKUP_NAME, asLookupMap("something", "55"), null, false);
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
        mdsRestFacade.get(new QueryParams(1, 10), false);
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

    @Test(expected = RestNoLookupResultException.class)
    public void shouldThrowExceptionForEmptyResult() {
        Map<String, String> lookupMap = asLookupMap(null, "44");
        QueryParams queryParams = mock(QueryParams.class);
        when(dataService.supportedLookup(null, 44, queryParams))
                .thenReturn(null);
        mdsRestFacade.executeLookup(SUPPORTED_LOOKUP_NAME, lookupMap, queryParams, false);
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
        record.setDateIgnoredByRest(new Date()); // dates will be ignored
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
