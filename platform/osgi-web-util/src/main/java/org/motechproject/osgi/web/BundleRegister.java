package org.motechproject.osgi.web;

import org.osgi.framework.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 *     The <code>BundleRegister</code> Singleton class is used for recording bundles.
 *     This class will help to reconfigure logger's levels.
 */
public final class BundleRegister {
    private static BundleRegister instance;
    private List<Bundle> bundleList;

     private BundleRegister() {
        bundleList = new ArrayList<>();
    }

    public static BundleRegister getInstance() {
        if (instance == null) {
            instance = new BundleRegister();
        }
        return instance;
    }

    public void addBundle(Bundle bundle) {
        bundleList.add(bundle);
    }

    public List<Bundle> getBundleList() {
        return bundleList;
    }
}
