package org.motechproject.outbox;

import org.motechproject.outbox.service.VoiceOutboxService;
import org.springframework.beans.factory.annotation.Autowired;


public class OutboxContext
{
    @Autowired
    private VoiceOutboxService voiceOutboxService;

    public VoiceOutboxService getVoiceOutboxService()
    {
        return voiceOutboxService;
    }

    public void setVoiceOutboxService(VoiceOutboxService voiceOutboxService)
    {
        this.voiceOutboxService = voiceOutboxService;
    }

    public static OutboxContext getInstance(){
		return instance;
	}

	private static OutboxContext instance = new OutboxContext();

	private OutboxContext(){}
}
