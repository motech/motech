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
package org.motechproject.core.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.dao.PatientDao;
import org.motechproject.model.Patient;
import org.motechproject.model.Visit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * PatientDAO Integration Tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationCommon.xml", "/persistenceIntegrationContext.xml"})
public class PatientDaoIT {

    @Autowired
    private PatientDao patientDao;

    private Patient patient;

    @Test
    public void testAdd() throws Exception {

        String id = "1_";

        Patient patient = new Patient();
        patient.setId(id);
        patientDao.add(patient);

        Patient savedPatient = patientDao.get(patient.getId());
        assertEquals(patient.getId(), savedPatient.getId());

        patientDao.remove(patient);

    }

    @Test
    public void testRemove() throws Exception {

        String id = "1_";

        Patient patient = new Patient();
        patient.setId(id);
        patient.setClinicPatientId("000");
        patientDao.add(patient);

        Patient savedPatient = patientDao.get(patient.getId());
        assertEquals(patient, savedPatient);

        patientDao.remove(patient);

        assertEquals(0, patientDao.getAll().size());

    }


    @Test
    public void testGetAllEmptyDB() throws Exception {

        List<Patient> patients = patientDao.getAll();
        assertEquals(0, patients.size());

    }

    @Test
    public void testAddRemoveVisit() throws Exception {

        String id = "1_";

        Patient patient = new Patient();
        patient.setId(id);
        patient.setClinicPatientId("000");
        patientDao.add(patient);

        Visit visit = new Visit();
        visit.setId("ao1");
        visit.setPatientId(id);
        visit.setVisitDate(new Date());
        patientDao.addVisit(visit);

        Patient savedPatient = patientDao.get(id);


        assertEquals(1, savedPatient.getVisits().size());

        patientDao.removeVisit(visit);

        savedPatient = patientDao.get(id);
        assertEquals(0, savedPatient.getVisits().size());

        patientDao.remove(savedPatient);
    }

    @Test
    public void testUpdateVisit() throws Exception {

        String id = "1_";

        Date initialVisitDate = new Date();
        Date newVisitDate =  new Date(initialVisitDate.getTime() + 10000);

        Patient patient = new Patient();
        patient.setId(id);
        patient.setClinicPatientId("000");
        patientDao.add(patient);

        Visit visit = new Visit();
        visit.setId("ao1");
        visit.setPatientId(id);
        visit.setVisitDate(initialVisitDate);
        patientDao.addVisit(visit);

        Patient savedPatient = patientDao.get(id);
        visit = savedPatient.getVisits().iterator().next();

        assertEquals(initialVisitDate, visit.getVisitDate());

        visit.setVisitDate(newVisitDate);
        patientDao.updateVisit(visit);

        savedPatient = patientDao.get(id);
        visit = savedPatient.getVisits().iterator().next();


        assertEquals(newVisitDate, visit.getVisitDate());

        patientDao.removeVisit(visit);

        savedPatient = patientDao.get(id);
        assertEquals(0, savedPatient.getVisits().size());

        patientDao.remove(savedPatient);
    }


    //The following tests have been developed to research ektorp

    /*
    //Method CouchDbRepositorySupport.getAll seems does not work
    //org.ektorp.DbAccessException: org.codehaus.jackson.map.exc.UnrecognizedPropertyException: Unrecognized field "dataObjectId" (Class org.motechproject.model.Patient), not marked as ignorable
    @Test
    public void testGetAll() throws Exception {

        String id = "1_";

        Patient patient = new Patient();
        patient.setId(id);
        patient .setClinicPatientId("000");
        patientDao.add(patient);

        List<Patient> patients = patientDao.getAll();
        assertEquals(1, patients.size());

        patientDao.remove(patient);

    }



    @Test
    //CouchDbRepositorySupport.remove throws NullPointerException
    public void testRemoveNotExist() throws Exception {

        String id = "1_";

        Patient patient = new Patient();
        patient.setId(id);

        patientDao.remove(patient);

    }

    @Test
    *//*org.ektorp.DbAccessException: 400:Bad Request
    URI: /patients/_1_
    Response Body:
    {
      "error" : "bad_request",
      "reason" : "Only reserved document ids may start with underscore."*//*

     public void testAddInvalidId() throws Exception {
        Patient patient = new Patient();
        patient.setId("_1_");
        patientDao.add(patient);

         Patient savedPatient = patientDao.get(patient.getId());
         assertEquals(patient, savedPatient);

    }

    @Test
    //org.ektorp.UpdateConflictException: document update conflict: id: unknown rev: unknown
    public void testAddDuplicate() throws Exception {
         String id = "1_";
        Patient patient = new Patient();
        patient.setId(id);
        Patient patient2 = new Patient();
         patient2.setId(id);
        patientDao.add(patient);
         try {
             patientDao.add(patient);
         } catch (Exception e) {
             throw e;
         } finally {
             patientDao.remove(patient);
         }
    }

    @Test
    //org.ektorp.DocumentNotFoundException: nothing found on db path: /patients/99, Response body: {"error":"not_found","reason":"missing"}
    public void testGetNotExist() throws Exception {

        Patient patient = patientDao.get("99");
        assertNull(patient);

    }
*/
    @Before @After
    public void cleanup() {
        for (Patient patient : patientDao.getAll()) {
            for (Visit visit : patient.getVisits()) {
                patientDao.removeVisit(visit);
            }
            patientDao.remove(patient);
        }
    }


}
