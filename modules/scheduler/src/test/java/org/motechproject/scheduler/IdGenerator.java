package org.motechproject.scheduler;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String id(String prefix) {
        return prefix + UUID.randomUUID().toString();
    }
}

