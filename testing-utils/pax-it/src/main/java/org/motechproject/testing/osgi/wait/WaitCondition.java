package org.motechproject.testing.osgi.wait;

/**
 * This interface allows providing custom wait conditions by implementing.
 */
public interface WaitCondition {

    /**
     * Should return true if we the wait should continue, false if we are done waiting.
     * @return true if waiting should continue, false otherwise
     */
    boolean needsToWait();
}
