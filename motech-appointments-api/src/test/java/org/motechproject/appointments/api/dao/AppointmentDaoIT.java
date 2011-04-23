/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.appointments.api.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointments.api.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Appointment DAO test
 * @author yyonkov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testIntegrationContext.xml"})
public class AppointmentDaoIT {
	@Autowired
	private AppointmentsDAO dao;

	@Test
	public void testCRUDandFindByParrentId() {
		String appId = "000111"; 
		String patientId = "0001";
		Appointment app = new Appointment();
		Appointment app1 = new Appointment();
		app.setId(appId);
		app1.setId("xxx");
		dao.addAppointment(app);
		dao.addAppointment(app1);
		app = dao.getAppointment(appId);
		assertNotNull(app);
//		app.setPatientArrived(true);
//		app.setPatientId(patientId);
//		dao.update(app);
//		Set<Appointment> apps = dao.get(patientId).getAppointment
//		assertNotNull(apps);
//		System.out.print(apps.size());
//		assertTrue(apps.size()==1);
		dao.removeAppointment(app);
		dao.removeAppointment(app1);
	}
}
