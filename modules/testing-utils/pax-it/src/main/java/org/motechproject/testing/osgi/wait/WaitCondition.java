package org.motechproject.testing.osgi.wait;

/**
 * This interface allows providing custom wait conditions by implementing.
 */
public interface WaitCondition {
    boolean needsToWait();
}
