#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.it;

import java.util.List;

import ${package}.domain.HelloWorldRecordDto;
import ${package}.service.HelloWorldRecordService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

/**
 * Verify that HelloWorldRecordService present, functional.
 */
public class HelloWorldRecordServiceIT extends BaseOsgiIT {

    public void testHelloWorldRecordService() throws Exception {
        ServiceReference registryReference = bundleContext.getServiceReference(HelloWorldRecordService.class.getName());
        assertNotNull(registryReference);
        HelloWorldRecordService helloRecordService = (HelloWorldRecordService) bundleContext
                .getService(registryReference);
        assertNotNull(helloRecordService);

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

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldRecordServiceITContext.xml" };
    }
}
