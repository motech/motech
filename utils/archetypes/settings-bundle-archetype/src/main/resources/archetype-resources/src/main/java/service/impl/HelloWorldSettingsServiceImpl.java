#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.impl;

import ${package}.service.HelloWorldSettingsService;

import org.motechproject.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link HelloWorldSettingsService} uses {@link SettingsFacade}.
 */
@Service("helloWorldSettingsService")
public class HelloWorldSettingsServiceImpl implements HelloWorldSettingsService {

    private Logger LOGGER = LoggerFactory.getLogger(HelloWorldSettingsServiceImpl.class.toString());

    @Autowired
    private SettingsFacade settingsFacade;

    @Override
    public String getSettingsValue(String key) {
        if (null == key) {
            return null;
        }
        return settingsFacade.getProperty(key);
    }

    @Override
    public void logInfoWithModuleSettings(String info) {
        String bundleName = getSettingsValue("${package}.bundle.name");
        String sampleSetting = getSettingsValue("${package}.sample.setting");
        LOGGER.info("{} (module name: {}, with sample setting: {})", new String[]{info, bundleName, sampleSetting});
    }
}
