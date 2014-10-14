package org.motechproject.mds.rest;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mds.testutil.records.Record;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RestProjectionTest {

    public static final List<String> REST_FIELDS = Arrays.asList("id", "creator", "owner", "value");

    @Test
    public void shouldCreateProjection() throws Exception {
        Record record = buildRecord(1L, "Big C", "Big C", "Big M", new DateTime(2014, 10, 9, 21, 15), new DateTime(2014, 10, 14, 11, 35), "recordValue", new Date());
        RestProjection expectedProjection = buildRestProjection(record);
        RestProjection actualProjection = RestProjection.createProjection(record, REST_FIELDS);
        assertEquals(expectedProjection, actualProjection);
    }

    @Test
    public void shouldCreateEmptyProjection() throws Exception {
        Record record = buildRecord(1L, "Big C", "Big C", "Big M", new DateTime(2014, 10, 9, 21, 15), new DateTime(2014, 10, 14, 11, 35), "recordValue", new Date());
        RestProjection expectedProjection = new RestProjection();
        RestProjection actualProjection = RestProjection.createProjection(record, Collections.<String>emptyList());
        assertEquals(expectedProjection, actualProjection);
    }

    @Test
    public void shouldCreateProjectionCollection() throws Exception {
        Record rest = buildRecord(1L, "Big D", "Big D", "Big N", new DateTime(2014, 1, 1, 21, 15), new DateTime(2014, 1, 2, 11, 35), "restValue", new Date());
        Record test = buildRecord(1L, "Big X", "Big E", "Big Q", new DateTime(2014, 1, 2, 21, 15), new DateTime(2014, 1, 3, 11, 35), "testValue", new Date());
        Record projection = buildRecord(1L, "Big Z", "Big D", "Big E", new DateTime(2014, 1, 3, 21, 15), new DateTime(2014, 1, 4, 11, 35), "projectionValue", new Date());

        List<Record> records = Arrays.asList(rest, test, projection);

        List<RestProjection> expectedProjections = Arrays.asList(buildRestProjection(rest), buildRestProjection(test), buildRestProjection(projection));
        List<RestProjection> actualProjections = RestProjection.createProjectionCollection(records, REST_FIELDS);

        assertEquals(expectedProjections, actualProjections);
    }

    private Record buildRecord(Long id, String creator, String owner, String modifiedBy, DateTime creationDate, DateTime modificationDate, String value, Date date) {
        Record record = new Record();
        record.setId(id);
        record.setCreator(creator);
        record.setOwner(owner);
        record.setModifiedBy(modifiedBy);
        record.setCreationDate(creationDate);
        record.setModificationDate(modificationDate);
        record.setValue(value);
        record.setDate(date);
        return record;
    }

    private RestProjection buildRestProjection(Record record) {
        RestProjection restProjection = new RestProjection();
        restProjection.put("id", record.getId());
        restProjection.put("creator", record.getCreator());
        restProjection.put("owner", record.getOwner());
        restProjection.put("value", record.getValue());
        return restProjection;
    }
}
