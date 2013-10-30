package settings.bundle.service;

public interface HelloWorldSettingsService {
    
    String getSettingsValue(String key);

    void logInfoWithModuleSettings(String info);

}
