package org.motechproject.outbox.api;

import org.motechproject.server.service.ivr.IVRSession;

public interface OutboxCommand {
	   String[] execute(IVRSession obj);
}
