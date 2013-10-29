#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import java.util.List;

import ${package}.domain.HelloWorldRecordDto;

public interface HelloWorldRecordService {

    void add(HelloWorldRecordDto record);

    HelloWorldRecordDto findByRecordName(String recordName);

    List<HelloWorldRecordDto> getRecords();

    void delete(HelloWorldRecordDto record);

}
