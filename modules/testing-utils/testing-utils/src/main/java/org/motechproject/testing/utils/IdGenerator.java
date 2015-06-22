package org.motechproject.testing.utils;

import java.util.UUID;

/**
 * Generates IDs by appending random UUIDs to the provided prefix.
 */
public final class IdGenerator {

    private IdGenerator() {
    }

    /**
     * Appends a random UUID to the provided prefix.
     * @param prefix the prefix of the id
     * @return the newly created id
     */
    public static String id(String prefix) {
        return prefix + UUID.randomUUID().toString();
    }
}

