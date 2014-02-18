package org.osgi.framework.hooks.weaving;

import java.util.ArrayList;
import java.util.List;

/**
 * This WovenClass class only defines methods used by MDS, since the goal here is to only get tests running.
 */
public class WovenClass {

    public String getClassName() {
        return "";
    }

    public void setBytes(byte[] bytes) {
    }

    public List<String> getDynamicImports() {
        return new ArrayList<>();
    }
}
