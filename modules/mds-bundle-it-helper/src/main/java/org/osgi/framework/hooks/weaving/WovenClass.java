package org.osgi.framework.hooks.weaving;

import java.util.List;

/**
 * This WovenClass class only defines methods used by MDS, since the goal here is to only get tests running.
 */
public interface WovenClass {

    String getClassName();

    void setBytes(byte[] bytes);

    List<String> getDynamicImports();
}
