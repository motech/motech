/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.motechproject.openmrs.web.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.openmrs.model.Appointment;
import org.motechproject.openmrs.model.AppointmentReminderPreferences;
import org.motechproject.openmrs.service.AppointmentReminderPreferenceService;
import org.motechproject.openmrs.service.AppointmentService;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppointmentController extends PortletController {

	/**
	 * Logger for this class and subclasses
	 */
	protected final Log log = LogFactory.getLog(getClass());

	private static final String MODEL_KEY_PREFERENCES = "preferences";
	
	@RequestMapping(value = "/module/motech/portlets/appointment.portlet", method = RequestMethod.GET)
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("in AppointmentController *******************");
		return super.handleRequest(request, response);
	}

	/**
	 * Gets a list of appointments for the given Patient
	 * 
	 * @param request
	 *            the HttpServletRequest with the date to look for Appointments
	 * @param model
	 *            ModelMap with Patient information
	 */
	@Override
	public void populateModel(HttpServletRequest request,
			Map<String, Object> model) {
		
		AppointmentReminderPreferenceService arpService = Context.getService(AppointmentReminderPreferenceService.class);
		
		Patient patient = (Patient) model.get("patient");

		try {
			String startDateString = ServletRequestUtils.getStringParameter(
					request, "windowStartDate");
			String endDateString = ServletRequestUtils.getStringParameter(
					request, "windowEndDate");

			if (startDateString != null && endDateString != null) {
				Date startDate = Context.getDateFormat().parse(startDateString);
				Date endDate = Context.getDateFormat().parse(endDateString);
				Appointment appointment = new Appointment();
				appointment.setPatient(patient);
				appointment.setWindowStartDate(startDate);
				appointment.setWindowEndDate(endDate);
				Context.getService(AppointmentService.class).saveAppointment(
						appointment);
			}

			String form = ServletRequestUtils.getStringParameter(
					request, "form");
			
			if (form != null && form.equals("preferences")) {
				// Retrieve relevant fields
				Boolean enableReminderService = ServletRequestUtils.getBooleanParameter(
						request, "enableReminderService");
				
				Integer daysBefore = ServletRequestUtils.getIntParameter(
						request, "daysBefore");
				Integer preferredTime = ServletRequestUtils.getIntParameter(
						request, "preferredTime");
				AppointmentReminderPreferences prefs = new AppointmentReminderPreferences();

				prefs.setModuleEnabled(enableReminderService);
				prefs.setPatient(patient);
				prefs.setDaysBefore(daysBefore);
				prefs.setPreferredTime(preferredTime);
				
				// Store preferences in OpenMRS DB
				arpService.saveAppointmentAppointmentReminderPreferences(prefs);
				// TODO: Serializes object into Motech DB
			}
			
			// Retrieve existing Appointment Reminder preferences (if they exist)
			AppointmentReminderPreferences prefs = arpService.getAppointmentReminderPreferencesByPatient(patient);
			if(prefs == null) {
				prefs = new AppointmentReminderPreferences(false, null, 0, 0);
			}
			
			model.put(MODEL_KEY_PREFERENCES, prefs);
			
			
		} catch (Exception ex) {
			log.error(ex);
		}

		List<Appointment> appointmentList = Context.getService(
				AppointmentService.class).getAppointments(patient);
		model.put("appointmentList", appointmentList);
	}

}

// public class AppointmentController{}