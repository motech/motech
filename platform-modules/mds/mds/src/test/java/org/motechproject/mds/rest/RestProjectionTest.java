package org.motechproject.mds.rest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.testutil.records.Record;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RestProjectionTest {

    public static final List<String> REST_FIELDS = Arrays.asList("id", "creator", "owner", "value", "blob");
    public static final List<String> REST_BLOB_FIELDS = Arrays.asList("blob");

    public Record recordOne;
    public Record recordTwo;
    public Record recordThree;

    public List<Record> records;

    @Before
    public void setUp() {

        recordOne = buildRecord(1L, "Big C", "Big C", "Big M", new DateTime(2014, 10, 9, 21, 15),
                new DateTime(2014, 10, 14, 11, 35), "recordValue", new Date(),
                ArrayUtils.toObject("TestBlobValueOne".getBytes()));

        recordTwo = buildRecord(1L, "Big X", "Big E", "Big Q", new DateTime(2014, 1, 2, 21, 15),
                new DateTime(2014, 1, 3, 11, 35), "testValue", new Date(),
                ArrayUtils.toObject("TestBlobValueTwo".getBytes()));

        recordThree = buildRecord(1L, "Big Z", "Big D", "Big E", new DateTime(2014, 1, 3, 21, 15),
                new DateTime(2014, 1, 4, 11, 35), "projectionValue", new Date(),
                ArrayUtils.toObject("TestBlobValueThree".getBytes()));

        records = Arrays.asList(recordOne, recordTwo, recordThree);
    }
    @Test
    public void shouldCreateProjection() throws Exception {

        RestProjection expectedProjection = buildRestProjection(recordOne);
        RestProjection actualProjection = RestProjection.createProjection(recordOne, REST_FIELDS, REST_BLOB_FIELDS);

        assertProjectionsEquals(expectedProjection, actualProjection);
    }

    @Test
    public void shouldCreateEmptyProjection() throws Exception {

        RestProjection expectedProjection = new RestProjection();
        RestProjection actualProjection = RestProjection.createProjection(recordOne, Collections.<String>emptyList(),
                Collections.<String>emptyList());

        assertProjectionsEquals(expectedProjection, actualProjection);
    }

    @Test
    public void shouldCreateProjectionCollection() throws Exception {

        List<RestProjection> expectedProjections = Arrays.asList(buildRestProjection(recordOne),
                buildRestProjection(recordTwo), buildRestProjection(recordThree));
        List<RestProjection> actualProjections = RestProjection.createProjectionCollection(records, REST_FIELDS,
                REST_BLOB_FIELDS);

        assertProjectionListsEquals(expectedProjections, actualProjections);
    }

    private Record buildRecord(Long id, String creator, String owner, String modifiedBy, DateTime creationDate,
                               DateTime modificationDate, String value, Date date, Byte[] blob) {

        Record record = new Record();
        record.setId(id);
        record.setCreator(creator);
        record.setOwner(owner);
        record.setModifiedBy(modifiedBy);
        record.setCreationDate(creationDate);
        record.setModificationDate(modificationDate);
        record.setValue(value);
        record.setDate(date);
        record.setBlob(blob);
        return record;
    }

    private RestProjection buildRestProjection(Record record) {
        RestProjection restProjection = new RestProjection();
        restProjection.put("id", record.getId());
        restProjection.put("creator", record.getCreator());
        restProjection.put("owner", record.getOwner());
        restProjection.put("value", record.getValue());
        restProjection.put("blob", Base64.encodeBase64(ArrayUtils.toPrimitive(record.getBlob())));
        return restProjection;
    }

    private void assertProjectionsEquals(RestProjection expected, RestProjection actual) {
        assertEquals(expected.get("id"), actual.get("id"));
        assertEquals(expected.get("creator"), actual.get("creator"));
        assertEquals(expected.get("owner"), actual.get("owner"));
        assertEquals(expected.get("value"), actual.get("value"));
        assertArrayEquals((byte[]) expected.get("blob"), (byte[]) actual.get("blob"));
    }

    private void assertProjectionListsEquals(List<RestProjection> expected, List<RestProjection> actuals) {
        for (int i = 0; i < expected.size(); i++) {
            assertProjectionsEquals(expected.get(i), actuals.get(i));
        }
    }
}
