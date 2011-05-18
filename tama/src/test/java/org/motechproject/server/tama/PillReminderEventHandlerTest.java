package org.motechproject.server.tama;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.pillreminder.api.EventKeys;
import org.motechproject.pillreminder.api.PillReminderService;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.motechproject.pillreminder.api.model.Schedule;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.motechproject.tama.api.dao.PatientDAO;
import org.motechproject.tama.api.model.Patient;
import org.motechproject.tama.api.model.Preferences;
import org.motechproject.tama.api.model.Preferences.Language;

@RunWith(MockitoJUnitRunner.class)
public class PillReminderEventHandlerTest {
	
    @InjectMocks
    PillReminderEventHandler pillReminderEventHandler = new PillReminderEventHandler();

    @Mock
    private PillReminderService pillReminderService;
    
    @Mock
    private PatientDAO patientDAO;

    @Mock
    private IVRService ivrService;
    
    private static final String VXML_URL = "http://localhost/";
	private static final String PILLREMINDER_ID = "001";
	private static final String PATIENT_ID = "pid12";
	private static final String PHONE_NUM = "604604";
	
	PillReminder reminder;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        reminder = new PillReminder();
        reminder.setId(PILLREMINDER_ID);		
		reminder.setStartDate(new DateTime(2011, 3, 1, 0, 0, 0, 0).toDate());
		reminder.setEndDate(new DateTime(2011, 3, 31, 0, 0, 0, 0).toDate());
		reminder.setExternalId(PATIENT_ID);
		
		Schedule schedule = new Schedule();
		schedule.setStartCallTime(new Time(7,0));
		schedule.setEndCallTime(new Time(9,0));
		schedule.setRepeatCount(5);
		schedule.setRepeatInterval(5*60);		
		reminder.getSchedules().add(schedule);
		
		schedule = new Schedule();

		schedule.setStartCallTime(new Time(11,0));
		schedule.setEndCallTime(new Time(13,0));
		schedule.setRepeatCount(5);
		schedule.setRepeatInterval(5*60);
		reminder.getSchedules().add(schedule);
		
		when(pillReminderService.getPillReminder(PILLREMINDER_ID)).thenReturn(reminder);
		
		Preferences pref = new Preferences();
		pref.setLanguage(Language.en);
		Patient patient = new Patient();
		patient.setId(PATIENT_ID);
		patient.setPhoneNumber(PHONE_NUM);
		patient.setPreferences(pref);
		
		when(patientDAO.get(PATIENT_ID)).thenReturn(patient);
		
		List<String> names = new ArrayList<String>();
		names.add("m1");
		names.add("m2");
		when(pillReminderService.getMedicinesWithinWindow(any(String.class), any(Date.class))).thenReturn(names);
		
		pillReminderEventHandler.setVxmlUrl(VXML_URL);
    }

    @Test
    public void testHandle_NoPRId() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        
        MotechEvent event = new MotechEvent("", params);
        pillReminderEventHandler.handle(event);
        verify(ivrService, times(0)).initiateCall(any(CallRequest.class));
    }

    @Test
    public void testHandle_NoPatient() throws Exception {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put(EventKeys.PILLREMINDER_ID_KEY, PILLREMINDER_ID);
    	reminder.setExternalId("no_id");
    	
    	MotechEvent event = new MotechEvent("", params);
    	pillReminderEventHandler.handle(event);
    	verify(ivrService, times(0)).initiateCall(any(CallRequest.class));
    }
    
    @Test
    public void testHandle_Normal() throws Exception {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put(EventKeys.PILLREMINDER_ID_KEY, PILLREMINDER_ID);
    	
    	MotechEvent event = new MotechEvent("", params);
    	pillReminderEventHandler.handle(event);
    	ArgumentCaptor<CallRequest> argument = ArgumentCaptor.forClass(CallRequest.class);
    	verify(ivrService, times(1)).initiateCall(argument.capture());
    	CallRequest callRequest = argument.getValue();
    	assertEquals(PHONE_NUM, callRequest.getPhone());
    	String expectedUrl = String.format(VXML_URL + "?" + pillReminderEventHandler.PATIENT_ID_PARAM + "=%s&" + pillReminderEventHandler.TREE_NAME_PARAM + "=%s&" + pillReminderEventHandler.LANGUAGE_PARAM + "=%s", PATIENT_ID, "m1,m2", Language.en.name());
    	assertEquals(expectedUrl, callRequest.getVxmlUrl());
    }

}
