package org.motechproject.appointments.api.context;

import org.motechproject.appointments.api.dao.AppointmentsDAO;
import org.motechproject.appointments.api.dao.RemindersDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentsContext
{
    @Autowired
    private AppointmentsDAO appointmentsDAO;

    @Autowired
    private RemindersDAO remindersDAO;

    public AppointmentsDAO getAppointmentsDAO()
    {
        return appointmentsDAO;
    }

    public void setAppointmentsDAO(AppointmentsDAO appointmentsDAO)
    {
        this.appointmentsDAO = appointmentsDAO;
    }

    public RemindersDAO getRemindersDAO()
    {
        return remindersDAO;
    }

    public void setRemindersDAO(RemindersDAO remindersDAO)
    {
        this.remindersDAO = remindersDAO;
    }

    public static AppointmentsContext getInstance(){
		return instance;
	}

	private static AppointmentsContext instance = new AppointmentsContext();

	private AppointmentsContext(){}
}
