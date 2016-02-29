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
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.entityinfo.EntityInfoReaderImpl;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.exception.rest.RestBadBodyFormatException;
import org.motechproject.mds.exception.rest.RestLookupExecutionForbiddenException;
import org.motechproject.mds.exception.rest.RestLookupNotFoundException;
import org.motechproject.mds.exception.rest.RestNoLookupResultException;
import org.motechproject.mds.exception.rest.RestOperationNotSupportedException;
import org.motechproject.mds.query.QueryParams;
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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
    private static final String TEST_MODULE = "test_module";
    private static final String ENTITY_NAME = "Record";
    private static final String NAMESPACE = "test_namespace";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EntityInfo entity;

    @Mock
    private Field valueField;

    @Mock
    private RestFacadeTestService dataService;

    @Mock
    private EntityInfoReaderImpl entityInfoReader;

    @Mock
    private RestOptionsDto restOptions;

    @Mock
    private AdvancedSettingsDto advancedSettingsDto;

    @InjectMocks
    private MdsRestFacadeImpl<Record> mdsRestFacade = new MdsRestFacadeImpl<>();

    private Record recordOne;

    private Byte[] blobFieldValue = ArrayUtils.toObject("TestBlobValue".getBytes());

    private byte[] encodedBlobField = Base64.encodeBase64(ArrayUtils.toPrimitive(blobFieldValue));

    @Before
    public void setUp() {
        when(dataService.getClassType()).thenReturn(Record.class);
        when(entityInfoReader.getEntityInfo(Record.class.getName())).thenReturn(entity);
        when(entity.getName()).thenReturn(ENTITY_NAME);
        when(entity.getModule()).thenReturn(TEST_MODULE);
        when(entity.getClassName()).thenReturn(Record.class.getName());
        when(entity.getNamespace()).thenReturn(NAMESPACE);
        when(entity.getAdvancedSettings()).thenReturn(advancedSettingsDto);
        when(advancedSettingsDto.getRestOptions()).thenReturn(restOptions);

        // set up rest fields
        FieldDto valueField = FieldTestHelper.fieldDto(3L, VALUE_FIELD, String.class.getName(), VALUE_FIELD, null);
        FieldDto dateField = FieldTestHelper.fieldDto(4L, DATE_FIELD, Date.class.getName(), DATE_FIELD, null);
        FieldDto blobField = FieldTestHelper.fieldDto(5L, BLOB_FIELD, Byte[].class.getName(), BLOB_FIELD, null);
        blobField.setType(new TypeDto("mds.field.blob", StringUtils.EMPTY, BLOB_FIELD, Byte[].class.getName()));
        when(restOptions.getFieldNames()).thenReturn(Arrays.asList(VALUE_FIELD, DATE_FIELD, BLOB_FIELD));

        // set up lookups
        FieldDto strField = FieldTestHelper.fieldDto(1L, STR_FIELD, String.class.getName(), STR_FIELD, null);
        FieldDto intField = FieldTestHelper.fieldDto(2L, INT_FIELD, Integer.class.getName(), INT_FIELD, null);
        when(entity.getFieldDtos()).thenReturn(asList(intField, strField, valueField, dateField, blobField));
        when(entity.getField(STR_FIELD)).thenReturn(FieldTestHelper.fieldInfo(STR_FIELD, String.class, false, true));
        when(entity.getField(INT_FIELD)).thenReturn(FieldTestHelper.fieldInfo(INT_FIELD, Integer.class, false, true));

        LookupDto forbiddenLookup = new LookupDto(FORBIDDEN_LOOKUP_NAME, true, false,
                asList(FieldTestHelper.lookupFieldDto(1L, STR_FIELD), FieldTestHelper.lookupFieldDto(2L, INT_FIELD)),
                true);
        LookupDto supportedLookup = new LookupDto(SUPPORTED_LOOKUP_NAME, false, true,
                asList(FieldTestHelper.lookupFieldDto(1L, STR_FIELD), FieldTestHelper.lookupFieldDto(2L, INT_FIELD)),
                true);
        when(entity.getLookups()).thenReturn(asList(forbiddenLookup, supportedLookup));

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

        RestResponse result = mdsRestFacade.get(queryParams, false);

        verify(dataService).retrieveAll(queryParams);

        assertEquals(1, result.getData().size());
        assertEquals(3, result.getData().get(0).size());
        assertEquals(recordOne.getValue(), result.getData().get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.getData().get(0).get(DATE_FIELD));
        assertNull(result.getData().get(0).get(BLOB_FIELD));
    }

    @Test
    public void shouldGetByIdWithoutBlobField() {
        setUpCrudAccess(false, true, false, false);

        RestResponse recResult = mdsRestFacade.get(1l, false);

        assertEquals(3, recResult.getData().get(0).size());
        assertEquals(recordOne.getValue(), recResult.getData().get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), recResult.getData().get(0).get(DATE_FIELD));
        assertNull(recResult.getData().get(0).get(BLOB_FIELD));

        ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
        verify(dataService).findById(longCaptor.capture());

        assertEquals(Long.valueOf(1), longCaptor.getValue());
    }

    @Test
    public void shouldAppendMetadata() {
        setUpCrudAccess(false, true, false, false);
        when(dataService.count()).thenReturn(81l);

        QueryParams queryParams = new QueryParams(5, 20, new Order("value", Order.Direction.DESC));

        RestResponse result = mdsRestFacade.get(queryParams, false);

        assertNotNull(result.getMetadata());
        assertEquals(ENTITY_NAME, result.getMetadata().getEntity());
        assertEquals(TEST_MODULE, result.getMetadata().getModule());
        assertEquals(Record.class.getName(), result.getMetadata().getClassName());
        assertEquals(NAMESPACE, result.getMetadata().getNamespace());
        assertEquals(5, result.getMetadata().getPage());
        assertEquals(20, result.getMetadata().getPageSize());
        assertEquals(81l, result.getMetadata().getTotalCount());
    }

    @Test
    public void shouldAppendMetadataWhenIdParamWasSpecified() {
        setUpCrudAccess(false, true, false, false);

        RestResponse result = mdsRestFacade.get(1l, false);

        assertNotNull(result.getMetadata());
        assertEquals(ENTITY_NAME, result.getMetadata().getEntity());
        assertEquals(TEST_MODULE, result.getMetadata().getModule());
        assertEquals(Record.class.getName(), result.getMetadata().getClassName());
        assertEquals(NAMESPACE, result.getMetadata().getNamespace());
        assertEquals(1, result.getMetadata().getPage());
        assertEquals(1, result.getMetadata().getPageSize());
        assertEquals(1l, result.getMetadata().getTotalCount());
    }

    @Test
    public void shouldGetByQueryParamsWithBlobField() {
        setUpCrudAccess(false, true, false, false);

        QueryParams queryParams = new QueryParams(5, 20, new Order("value", Order.Direction.DESC));

        RestResponse result = mdsRestFacade.get(queryParams, true);

        verify(dataService).retrieveAll(queryParams);

        assertEquals(1, result.getData().size());
        assertEquals(3, result.getData().get(0).size());
        assertEquals(recordOne.getValue(), result.getData().get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.getData().get(0).get(DATE_FIELD));
        assertArrayEquals(encodedBlobField, (byte[]) result.getData().get(0).get(BLOB_FIELD));
    }

    @Test
    public void shouldGetByIdWithBlobField() {

        setUpCrudAccess(false, true, false, false);

        RestResponse result = mdsRestFacade.get(1l, true);

        assertEquals(3, result.getData().get(0).size());
        assertEquals(recordOne.getValue(), result.getData().get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.getData().get(0).get(DATE_FIELD));
        assertArrayEquals(encodedBlobField, (byte[]) result.getData().get(0).get(BLOB_FIELD));

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

        RestResponse result = (RestResponse) mdsRestFacade.executeLookup(SUPPORTED_LOOKUP_NAME, lookupMap, queryParams, false);

        assertEquals(1, result.getData().size());
        assertEquals(3, result.getData().get(0).size());
        assertEquals(recordOne.getValue(), result.getData().get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.getData().get(0).get(DATE_FIELD));
        assertNull(result.getData().get(0).get(BLOB_FIELD));

        verify(dataService).supportedLookup(null, 44, queryParams);
    }

    @Test
    public void shouldExecuteLookupWithBlobField() {

        Map<String, String> lookupMap = asLookupMap(null, "44");
        QueryParams queryParams = mock(QueryParams.class);

        when(dataService.supportedLookup(null, 44, queryParams))
                .thenReturn(asList(recordOne));

        RestResponse result = (RestResponse) mdsRestFacade.executeLookup(SUPPORTED_LOOKUP_NAME, lookupMap, queryParams, true);

        assertEquals(1, result.getData().size());
        assertEquals(3, result.getData().get(0).size());
        assertEquals(recordOne.getValue(), result.getData().get(0).get(VALUE_FIELD));
        assertEquals(recordOne.getDate(), result.getData().get(0).get(DATE_FIELD));
        assertArrayEquals((byte[]) result.getData().get(0).get(BLOB_FIELD), encodedBlobField);

        verify(dataService).supportedLookup(null, 44, queryParams);
    }

    @Test
    public void shouldAppendMetadataWhenExecutingLookup() {
        when(dataService.countSupportedLookup(anyString(), anyInt())).thenReturn(81l);
        Map<String, String> lookupMap = asLookupMap(null, "44");
        QueryParams queryParams = new QueryParams(5, 20, new Order("value", Order.Direction.DESC));

        when(dataService.supportedLookup(null, 44, queryParams))
                .thenReturn(asList(recordOne));

        RestResponse result = (RestResponse) mdsRestFacade.executeLookup(SUPPORTED_LOOKUP_NAME, lookupMap, queryParams, false);

        assertNotNull(result.getMetadata());
        assertEquals(ENTITY_NAME, result.getMetadata().getEntity());
        assertEquals(TEST_MODULE, result.getMetadata().getModule());
        assertEquals(Record.class.getName(), result.getMetadata().getClassName());
        assertEquals(NAMESPACE, result.getMetadata().getNamespace());
        assertEquals(5, result.getMetadata().getPage());
        assertEquals(20, result.getMetadata().getPageSize());
        assertEquals(81l, result.getMetadata().getTotalCount());
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

    @Test(expected = RestLookupExecutionForbiddenException.class)
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
        when(restOptions.isCreate()).thenReturn(allowCreate);
        when(restOptions.isRead()).thenReturn(allowRead);
        when(restOptions.isUpdate()).thenReturn(allowUpdate);
        when(restOptions.isDelete()).thenReturn(allowDelete);
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
        long countSupportedLookup(String strField, Integer intField);
    }
}
