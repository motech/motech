package org.motechproject.appointments.api.context;

import org.motechproject.appointments.api.dao.AppointmentsDAO;
import org.springframework.beans.factory.annotation.Autowired;


public class AppointmentReminderContext
{
    @Autowired
    private AppointmentsDAO appointmentsDAO;

    public AppointmentsDAO getAppointmentsDAO()
    {
        return appointmentsDAO;
    }

    public void setAppointmentsDAO(AppointmentsDAO appointmentsDAO)
    {
        this.appointmentsDAO = appointmentsDAO;
    }

    public static AppointmentReminderContext getInstance(){
		return instance;
	}

	private static AppointmentReminderContext instance = new AppointmentReminderContext();

	private AppointmentReminderContext(){}
}
