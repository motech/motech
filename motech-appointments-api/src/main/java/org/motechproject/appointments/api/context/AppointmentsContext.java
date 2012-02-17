package org.motechproject.appointments.api.context;

import org.motechproject.appointments.api.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentsContext
{
    @Autowired
    private ReminderService reminderService;

    public ReminderService getReminderService()
    {
        return reminderService;
    }

    public void setReminderService(ReminderService reminderService)
    {
        this.reminderService = reminderService;
    }

    public static AppointmentsContext getInstance(){
		return instance;
	}

	private static AppointmentsContext instance = new AppointmentsContext();

	private AppointmentsContext(){}
}
