package org.motechproject.admin.bundles;

import org.osgi.framework.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class representing a filter for {@link Bundle} instances. Used by the
 * {@link org.motechproject.admin.service.ModuleAdminService} to determine which {@link Bundle}s should be
 * returned by it's listing methods.
 */
public abstract class MotechBundleFilter {

    /**
     * Filters the given {@link Bundle} array and returns a {@link List} containing entries that pass criteria.
     * Only entries for which the {@link #passesCriteria(org.osgi.framework.Bundle)} method returns true will
     * be part of the returned {@link List}.
     *
     * @param bundles the array of {@link Bundle} objects to be filtered.
     * @return a {@link List} containing the {@link Bundle} objects that pass the filter criteria.
     * @see #passesCriteria(org.osgi.framework.Bundle)
     */
    public List<Bundle> filter(Bundle[] bundles) {
        List<Bundle> result = new ArrayList<>();

        if (bundles != null) {
            for (Bundle bundle : bundles) {
                if (passesCriteria(bundle)) {
                    result.add(bundle);
                }
            }
        }

        return result;
    }

    /**
     * A method which has to be implemented by child classes. Determines whether the {@link Bundle} passes
     * the filter criteria and should pass filtering.
     * @param bundle the {@link Bundle} to be filtered.
     * @return true if the {@link Bundle} passes filter criteria, false otherwise.
     */
    public abstract boolean passesCriteria(Bundle bundle);
}
