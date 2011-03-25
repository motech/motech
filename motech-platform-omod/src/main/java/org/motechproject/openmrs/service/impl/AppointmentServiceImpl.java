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
package org.motechproject.openmrs.service.impl;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.motechproject.dao.PatientDao;
import org.motechproject.openmrs.dao.AppointmentDAO;
import org.motechproject.openmrs.model.Appointment;
import org.motechproject.openmrs.service.AppointmentService;
import org.openmrs.Patient;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.springframework.beans.factory.annotation.Autowired;

public class AppointmentServiceImpl extends BaseOpenmrsService implements
		AppointmentService {

	private AppointmentDAO appointmentDao;

	@Autowired(required=false)
	private PatientDao motechPatientDao;

	@Override
	public Appointment getAppointment(Integer id) {
		return appointmentDao.getAppointment(id);
	}

	@Override
	public List<Appointment> getAppointments(Patient patient) {
		return appointmentDao.getAppointments(patient);
	}

	@Override
	public Appointment saveAppointment(Appointment appointment) {
		appointment = appointmentDao.saveAppointment(appointment);
		if (motechPatientDao != null) {
			// save the appointment into motech's data store
			org.motechproject.model.Appointment mAppointment = new org.motechproject.model.Appointment();
			try {
				
				//BeanUtils.copyProperties(mAppointment, appointment);

				mAppointment.setId(appointment.getUuid());
				mAppointment.setArrivalDate(appointment.getArrivalDate());
				mAppointment.setPatientArrived(appointment.getPatientArrived());
				mAppointment.setWindowStartDate(appointment.getWindowStartDate());
				mAppointment.setWindowEndDate(appointment.getWindowEndDate());
				mAppointment.setPatientId(appointment.getPatient().getId().toString());
				
				motechPatientDao.addAppointment(mAppointment);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return appointment;
	}

	@Override
	public void setAppointmentDAO(AppointmentDAO dao) {
		this.appointmentDao = dao;
	}

}