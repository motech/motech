package org.osgi.framework.hooks.weaving;

/**
 * This is a copy of the actual WeavingHook interface from OSGi 4.3.
 */
public interface WeavingHook {

    void weave(WovenClass wovenClass);
}


