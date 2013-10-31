#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.domain;

/**
 * Interface for simple records to store in repository.
 */
public interface HelloWorldRecord {

    String getName();

    String getMessage();
}
