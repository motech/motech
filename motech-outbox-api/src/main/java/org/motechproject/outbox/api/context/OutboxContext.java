package org.motechproject.outbox.api.context;

import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.springframework.beans.factory.annotation.Autowired;


public final class OutboxContext {

    @Autowired
    private VoiceOutboxService voiceOutboxService;

    public VoiceOutboxService getVoiceOutboxService() {
        return voiceOutboxService;
    }

    public static OutboxContext getInstance() {
        return instance;
    }

    private static OutboxContext instance = new OutboxContext();

    private OutboxContext() {

    }
}
