package org.motechproject.testing.osgi;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class MotechBundleSorter {

    public static String[] sort(String[] bundles) {
        List<String> bundlesList = Arrays.asList(bundles);
        Collections.sort(bundlesList, new Comparator<String>() {

            @Override
            public int compare(String bundle1, String bundle2) {
                String[] bundle1Parts = bundle1.split(",");
                String[] bundle2Parts = bundle2.split(",");
                if (bundle1Parts.length < 2 || bundle2Parts.length < 2) {
                    return bundle1.compareTo(bundle2);
                }

                String bundle1ArtifactName = bundle1Parts[1];
                String bundle2ArtifactName = bundle2Parts[1];

                return compareBundles(bundle1, bundle2, bundle1ArtifactName, bundle2ArtifactName);
            }

            private int compareBundles(String bundle1, String bundle2, String bundle1ArtifactName, String bundle2ArtifactName) {
                if (!isMotechBundle(bundle1ArtifactName) && !isMotechBundle(bundle2ArtifactName)) {
                    // non motech bundles - sort as usual
                    return bundle1.compareTo(bundle2);
                } else if (isMotechBundle(bundle1ArtifactName) && !isMotechBundle(bundle2ArtifactName)) {
                    return 1;
                } else if (!isMotechBundle(bundle1ArtifactName) && isMotechBundle(bundle2ArtifactName)) {
                    return -1;
                } else if (isMotechPlatformBundle(bundle1ArtifactName)) {
                    return firstOneisPlatformBundle(bundle1, bundle2, bundle2ArtifactName);
                } else if (isMotechPlatformBundle(bundle2ArtifactName)) {
                    // second bundle is a motech platform bundle
                    if (isMotechModuleBundle(bundle1ArtifactName)) {
                        // first is a motech module bundle - give platform
                        // priority
                        return 1;
                    }

                    // first bundle is non motech - give it priority
                    return -1;
                }

                // this would be reached if both bundles are motech module
                // bundles - sort as usual
                return bundle1.compareTo(bundle2);
            }

            private int firstOneisPlatformBundle(String bundle1, String bundle2, String bundle2ArtifactName) {
                // first bundle is a motech platform bundle
                if (isMotechPlatformBundle(bundle2ArtifactName)) {
                    // both are platform bundles - sort as usual
                    return bundle1.compareTo(bundle2);
                }

                if (isMotechModuleBundle(bundle2ArtifactName)) {
                    // second is motech module bundle - give priority to
                    // platform
                    return -1;
                }

                // second bundle is non motech - give it priority
                return 1;
            }
        });
        return bundlesList.toArray(new String[]{});
    }

    private static boolean isMotechBundle(String bundleName) {
        return bundleName.startsWith("motech-");
    }

    private static boolean isMotechPlatformBundle(String bundleName) {
        return isMotechBundle(bundleName) && bundleName.startsWith("motech-platform");
    }

    private static boolean isMotechModuleBundle(String bundleName) {
        return isMotechBundle(bundleName) && !bundleName.startsWith("motech-platform");
    }

    private MotechBundleSorter() {
    }
}
