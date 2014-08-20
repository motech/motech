#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.osgi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * HelloWorld bundle integration tests suite.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        HelloWorldServiceIT.class#if($http == 'true'),
        HelloWorldWebIT.class#end#if($repository == 'true'),
        HelloWorldRecordServiceIT.class#end#if($settings == 'true'),
        HelloWorldSettingsServiceIT.class#end

})
public class HelloWorldIntegrationTests {
}
