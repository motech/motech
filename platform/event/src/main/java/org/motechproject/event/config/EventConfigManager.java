package org.motechproject.event.config;

import java.util.Properties;

public interface EventConfigManager {

    Properties getActivemqConfig();

    String getActivemqConfigLocation();

    void setActivemqConfigLocation(String activemqConfigLocation);
}
