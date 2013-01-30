package org.motechproject.admin.bundles;

import org.osgi.framework.Bundle;

import java.util.List;

public interface MotechBundleFilter {

    List<Bundle> filter(Bundle[] bundles);

    boolean passesCriteria(Bundle bundle);
}
