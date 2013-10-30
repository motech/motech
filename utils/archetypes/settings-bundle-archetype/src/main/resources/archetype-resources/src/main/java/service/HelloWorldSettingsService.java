#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

public interface HelloWorldSettingsService {
    
    String getSettingsValue(String key);

    void logInfoWithModuleSettings(String info);

}
