package org.motechproject.mds.testJdoDiscriminator.osgi;

import org.motechproject.mds.testJdoDiscriminator.domain.HelloWorldRecord;
import org.motechproject.mds.testJdoDiscriminator.service.HelloWorldRecordService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Verify that HelloWorldRecordService present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class HelloWorldRecordServiceIT extends BasePaxIT {

    @Inject
    private HelloWorldRecordService helloRecordService;

    @Test
    public void testHelloWorldRecordService() throws Exception {
        HelloWorldRecord testRecord = new HelloWorldRecord("testName", "test message");
        helloRecordService.add(testRecord);

        HelloWorldRecord record = helloRecordService.findRecordByName(testRecord.getName());
        assertEquals(testRecord, record);

        List<HelloWorldRecord> records = helloRecordService.getRecords();
        assertTrue(records.contains(testRecord));

        helloRecordService.delete(testRecord);
        record = helloRecordService.findRecordByName(testRecord.getName());
        assertNull(record);
    }
}
