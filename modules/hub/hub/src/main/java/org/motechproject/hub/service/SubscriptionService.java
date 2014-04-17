package org.motechproject.hub.service;

import org.motechproject.hub.exception.HubException;
import org.motechproject.hub.model.Modes;

public interface SubscriptionService {

	public void subscribe(String callbackUrl, Modes hubMode, String topic, String leaseSeconds, String secret) throws HubException;
}
