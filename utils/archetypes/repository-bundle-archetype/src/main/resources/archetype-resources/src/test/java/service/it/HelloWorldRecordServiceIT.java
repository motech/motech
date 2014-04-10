#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.it;

import java.util.List;

import ${package}.domain.HelloWorldRecordDto;
import ${package}.service.HelloWorldRecordService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.motechproject.testing.osgi.BasePaxIT;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Verify that HelloWorldRecordService present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class HelloWorldRecordServiceIT extends BasePaxIT {

    @Inject
    private HelloWorldRecordService helloRecordService;

    @Test
    public void testHelloWorldRecordService() throws Exception {
        HelloWorldRecordDto testRecord = new HelloWorldRecordDto("testName", "test message");
        helloRecordService.add(testRecord);

        HelloWorldRecordDto record = helloRecordService.findByRecordName(testRecord.getName());
        assertEquals(testRecord, record);

        List<HelloWorldRecordDto> records = helloRecordService.getRecords();
        assertTrue(records.contains(testRecord));

        helloRecordService.delete(testRecord);
        record = helloRecordService.findByRecordName(testRecord.getName());
        assertNull(record);
    }
}
