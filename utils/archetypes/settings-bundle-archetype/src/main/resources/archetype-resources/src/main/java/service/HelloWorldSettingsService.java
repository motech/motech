#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

/**
 * Service interface for getting and logging module settings.
 */
public interface HelloWorldSettingsService {
    
    String getSettingsValue(String key);

    void logInfoWithModuleSettings(String info);

}
