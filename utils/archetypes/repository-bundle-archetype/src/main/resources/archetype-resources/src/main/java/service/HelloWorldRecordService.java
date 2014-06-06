#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import java.util.List;

import ${package}.domain.HelloWorldRecord;

/**
 * Service interface for CRUD on simple repository records.
 */
public interface HelloWorldRecordService {

    void create(String name, String message);

    void add(HelloWorldRecord record);

    HelloWorldRecord findRecordByName(String recordName);

    List<HelloWorldRecord> getRecords();

    void delete(HelloWorldRecord record);

    void update(HelloWorldRecord record);
}
