package org.motechproject.outbox.api.context;

import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.springframework.beans.factory.annotation.Autowired;


public class OutboxContext
{
    @Autowired
    private OutboundVoiceMessageDao outboundVoiceMessageDao;

    public OutboundVoiceMessageDao getOutboundVoiceMessageDao()
    {
        return outboundVoiceMessageDao;
    }

    public void setOutboundVoiceMessageDao(OutboundVoiceMessageDao outboundVoiceMessageDao)
    {
        this.outboundVoiceMessageDao = outboundVoiceMessageDao;
    }

    public static OutboxContext getInstance(){
		return instance;
	}

	private static OutboxContext instance = new OutboxContext();

	private OutboxContext(){}
}
