package org.motechproject.server.service;

/**
 * Created by IntelliJ IDEA.
 * User: az44
 * Date: 24/03/11
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AppointmentReminderService {
    void remindPatientAppointment(String appointmentId);
}
