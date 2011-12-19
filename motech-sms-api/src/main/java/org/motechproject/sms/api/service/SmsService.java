package org.motechproject.sms.api.service;

import java.util.List;

public interface SmsService {
	void sendSMS(List<String> recipients, String message);

	void sendSMS(String recipient, String message);
}
