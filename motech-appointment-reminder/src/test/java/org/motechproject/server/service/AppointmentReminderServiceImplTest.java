package org.motechproject.server.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.dao.PatientDao;
import org.motechproject.model.Appointment;
import org.motechproject.model.InitiateCallData;
import org.motechproject.model.Patient;
import org.motechproject.server.service.ivr.IVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

/**
 * Appointment Reminder Service Unit tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/ApplicationContext.xml"})
public class AppointmentReminderServiceImplTest {

    @Autowired
    private AppointmentReminderServiceImpl appointmentReminderService;

    @Test
    public void testRemindPatientAppointment() throws Exception {

        IVRService ivrServiceMock = mock(IVRService.class);
        PatientDao patientDaoMock = mock(PatientDao.class);

        Appointment appointment = new Appointment();
        appointment.setPatientId("1p");

        Patient patient = new Patient();
        patient.setPhoneNumber("1001");

        when(patientDaoMock.getAppointment(Mockito.anyString())).thenReturn(appointment);
        when(patientDaoMock.get(appointment.getPatientId())).thenReturn(patient);

        appointmentReminderService.setIvrService(ivrServiceMock);
        appointmentReminderService.setPatientDao(patientDaoMock);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(ivrServiceMock, times(1)).initiateCall(Mockito.any(InitiateCallData.class));


    }
}
