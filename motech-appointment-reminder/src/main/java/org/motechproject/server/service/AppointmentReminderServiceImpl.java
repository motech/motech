package org.motechproject.server.service;

import org.motechproject.context.Context;
import org.motechproject.dao.PatientDao;
import org.motechproject.model.Appointment;
import org.motechproject.model.InitiateCallData;
import org.motechproject.model.Patient;
import org.motechproject.server.ruleengine.KnowledgeBaseManager;
import org.motechproject.server.service.ivr.IVRService;
import org.springframework.stereotype.Service;

/**
 *
 */
public class AppointmentReminderServiceImpl implements AppointmentReminderService {


    IVRService ivrService;
    PatientDao patientDao;

    int timeOut;
    public final static String SCHEDULE_APPOINTMENT_REMINDER = "ScheduleAppointmentReminder";

    @Override
    public void remindPatientAppointment(String appointmentId) {

        Appointment appointment = patientDao.getAppointment(appointmentId);
        Patient patient = patientDao.get(appointment.getPatientId());

        long messageId = 1;
        String phone = patient.getPhoneNumber();

        //TODO - implement rules to determine reminder vxml URL
        KnowledgeBaseManager knowledgeBaseManager = Context.getInstance().getKnowledgeBaseManager();
        //Interim implementation
        String  appointmentReminderVmlUrl = "http://10.0.1.29:8080/TamaIVR/reminder/doc";


        InitiateCallData initiateCallData = new InitiateCallData(messageId, phone, timeOut, appointmentReminderVmlUrl);

        ivrService.initiateCall(initiateCallData);
    }

    void setIvrService(IVRService ivrService) {
        this.ivrService = ivrService;
    }

    void setPatientDao(PatientDao patientDao) {
        this.patientDao = patientDao;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }


}
