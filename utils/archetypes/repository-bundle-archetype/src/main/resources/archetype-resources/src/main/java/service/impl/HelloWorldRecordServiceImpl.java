#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.impl;

import ${package}.service.HelloWorldRecordService;
import ${package}.repository.HelloWorldRecordsDataService;
import ${package}.domain.HelloWorldRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link HelloWorldRecordService} interface. Uses
 * {@link HelloWorldRecordsDataService} in order to retrieve and persist records.
 */
@Service("helloWorldRecordService")
public class HelloWorldRecordServiceImpl implements HelloWorldRecordService {

    @Autowired
    private HelloWorldRecordsDataService helloWorldRecordsDataService;

    @Override
    public void create(String name, String message) {
        helloWorldRecordsDataService.create(
                new HelloWorldRecord(name, message)
        );
    }

    @Override
    public void add(HelloWorldRecord record) {
        helloWorldRecordsDataService.create(record);
    }

    @Override
    public HelloWorldRecord findRecordByName(String recordName) {
        HelloWorldRecord record = helloWorldRecordsDataService.findRecordByName(recordName);
        if (null == record) {
            return null;
        }
        return record;
    }

    @Override
    public List<HelloWorldRecord> getRecords() {
        return helloWorldRecordsDataService.retrieveAll();
    }

    @Override
    public void update(HelloWorldRecord record) {
        helloWorldRecordsDataService.update(record);
    }

    @Override
    public void delete(HelloWorldRecord record) {
        helloWorldRecordsDataService.delete(record);
    }
}
