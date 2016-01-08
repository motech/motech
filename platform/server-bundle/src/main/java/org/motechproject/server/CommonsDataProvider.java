package org.motechproject.server;

import org.motechproject.commons.api.AbstractDataProvider;
import org.motechproject.server.commons.PlatformCommons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The <code>CommonsDataProvider</code> responsible for providing commons values as current date
 * or MOTECH version as a data source in Tasks module.
 */
public class CommonsDataProvider extends AbstractDataProvider {

    private PlatformCommons platformCommons;

    @Autowired
    public CommonsDataProvider(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("commons-data-provider.json");
        if(resource != null) {
            setBody(resource);
        }
    }

    @Autowired
    public void setPlatformCommons(PlatformCommons platformCommons) {
        this.platformCommons = platformCommons;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        List<Class<?>> list = new ArrayList<>();
        list.add(PlatformCommons.class);
        return list;
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.server.commons";
    }

    @Override
    public String getName() {
        return "MOTECH Commons";
    }

    @Override
    public Object lookup(String type, String lookupName, Map<String, String> lookupFields) {
        if(supports(type)) {
            return platformCommons;
        }
        return null;
    }
}
