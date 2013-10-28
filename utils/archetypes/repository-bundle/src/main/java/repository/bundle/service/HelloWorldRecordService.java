package repository.bundle.service;

import java.util.List;

import repository.bundle.domain.HelloWorldRecordDto;

public interface HelloWorldRecordService {

    void add(HelloWorldRecordDto record);

    HelloWorldRecordDto findByRecordName(String recordName);

    List<HelloWorldRecordDto> getRecords();

    void delete(HelloWorldRecordDto record);

}
