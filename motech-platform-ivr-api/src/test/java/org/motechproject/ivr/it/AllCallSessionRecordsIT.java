package org.motechproject.ivr.it;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.domain.CallSessionRecord;
import org.motechproject.ivr.repository.AllCallSessionRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationPlatformIVR.xml")
public class AllCallSessionRecordsIT {

    @Autowired
    private AllCallSessionRecords allCallSessionRecords;

    @Test
    public void shouldCreateACallSessionRecordInDb() {
        CallSessionRecord callSessionRecord = new CallSessionRecord("session1");
        ArrayList<String> value = new ArrayList<String>();
        value.add("value1");
        value.add("value2");
        value.add("value3");
        callSessionRecord.add("key", value);

        allCallSessionRecords.add(callSessionRecord);

        List<CallSessionRecord> callSessionRecords = allCallSessionRecords.getAll();
        assertThat(callSessionRecords.size(), is(1));

        CallSessionRecord actualRecord = callSessionRecords.get(0);
        assertThat(actualRecord, is(callSessionRecord));
        List<String> values = actualRecord.<ArrayList<String>>valueFor("key");
        assertThat(values.size(), is(3));
        assertThat(values.get(0), is("value1"));
        assertThat(values.get(1), is("value2"));
        assertThat(values.get(2), is("value3"));
    }

    @Test
    public void shouldFindOrCreateACallSessionRecord() {
        assertThat(allCallSessionRecords.getAll().size(), is(0));
        CallSessionRecord newRecord = allCallSessionRecords.findOrCreate("session1");
        assertThat(allCallSessionRecords.getAll().size(), is(1));

        CallSessionRecord existingRecord = allCallSessionRecords.findOrCreate("session1");
        assertThat(allCallSessionRecords.getAll().size(), is(1));
        assertThat(existingRecord, is(equalTo(newRecord)));
    }

    @Test
    public void shouldIgnoreCaseWhileFindOrCreateACallSessionRecord() {
        assertThat(allCallSessionRecords.getAll().size(), is(0));
        allCallSessionRecords.findOrCreate("Session1");
        assertThat(allCallSessionRecords.getAll().size(), is(1));

        allCallSessionRecords.findOrCreate("SESSION1");
        assertThat(allCallSessionRecords.getAll().size(), is(1));
    }

    @Test
    public void shouldFindBySessionId() {
        CallSessionRecord callSessionRecord1 = new CallSessionRecord("S1");
        CallSessionRecord callSessionRecord2 = new CallSessionRecord("S2");

        allCallSessionRecords.add(callSessionRecord1);
        allCallSessionRecords.add(callSessionRecord2);

        CallSessionRecord actualRecord = allCallSessionRecords.findBySessionId("s1");

        assertNotNull(actualRecord);
        assertThat(actualRecord, is(callSessionRecord1));

        CallSessionRecord nonExistentRecord = allCallSessionRecords.findBySessionId("s3");
        assertNull(nonExistentRecord);
    }

    @Test
    public void shouldIgnoreCaseWhileFindingBySessionId() {
        CallSessionRecord callSessionRecord = new CallSessionRecord("IGNORE-CASE");

        allCallSessionRecords.add(callSessionRecord);

        CallSessionRecord actualRecord = allCallSessionRecords.findBySessionId("ignore-CASE");

        assertNotNull(actualRecord);
        assertThat(actualRecord, is(callSessionRecord));
    }

    @Test
    public void shouldUpdateTheExistingRecord() {
        CallSessionRecord callSessionRecord1 = new CallSessionRecord("S1");

        allCallSessionRecords.add(callSessionRecord1);
        CallSessionRecord actualRecord = allCallSessionRecords.findBySessionId("s1");
        assertNull(actualRecord.valueFor("k1"));

        actualRecord.add("k1", "v1");
        allCallSessionRecords.update(actualRecord);

        CallSessionRecord updatedRecord = allCallSessionRecords.findBySessionId("s1");
        assertNotNull(updatedRecord.valueFor("k1"));
    }

    @After
    public void tearDown() {
        allCallSessionRecords.removeAll();
    }
}
