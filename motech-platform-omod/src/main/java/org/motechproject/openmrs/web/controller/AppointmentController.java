/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
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
		} catch (Exception ex) {
			log.error(ex);
		}

		List<Appointment> appointmentList = Context.getService(
				AppointmentService.class).getAppointments(patient);
		model.put("appointmentList", appointmentList);
	}

}

// public class AppointmentController{}