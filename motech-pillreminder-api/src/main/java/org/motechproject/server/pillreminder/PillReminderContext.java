package org.motechproject.server.pillreminder;

import org.springframework.beans.factory.annotation.Autowired;

public class PillReminderContext {
	@Autowired
	private PillReminderService pillReminderService;

	public PillReminderService getPillReminderService() {
		return pillReminderService;
	}

	public void setPillReminderService(PillReminderService pillReminderService) {
		this.pillReminderService = pillReminderService;
	}

	public static PillReminderContext getInstance() {
		return instance;
	}

	private static PillReminderContext instance = new PillReminderContext();

	private PillReminderContext() {
	}
}
