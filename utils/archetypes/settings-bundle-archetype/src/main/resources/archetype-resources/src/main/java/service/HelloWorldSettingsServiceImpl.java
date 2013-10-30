#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("helloWorldSettingsService")
public class HelloWorldSettingsServiceImpl implements HelloWorldSettingsService {

    private Logger logger = LoggerFactory.getLogger(HelloWorldSettingsServiceImpl.class.toString());

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
        String bundleName = getSettingsValue("${package}.sample.setting");
        String sampleSetting = getSettingsValue("${package}.bundle.name");
        logger.info("{} (module name: {}, with sample setting: {})", new String[] { info, bundleName, sampleSetting });
    }
}
