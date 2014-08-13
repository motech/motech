package org.motechproject.testing.osgi.framework;

import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.util.Map;

/**
 *  <code>BundleErrorAwareFrameworkFactory</code> is a framework factory decorator that
 *  produces <code>Framework</code> objects decorated with <code>BundleErrorAwareFramework</code>.
 */
public class BundleErrorAwareFrameworkFactory implements FrameworkFactory {

    private FrameworkFactory frameworkFactory;

    public BundleErrorAwareFrameworkFactory(FrameworkFactory frameworkFactory) {
        this.frameworkFactory = frameworkFactory;
    }

    @Override
    public Framework newFramework(Map<String, String> configuration) {
        return new BundleErrorAwareFramework(frameworkFactory.newFramework(configuration));
    }
}
