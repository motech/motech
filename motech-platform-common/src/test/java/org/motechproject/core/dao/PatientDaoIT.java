package org.motechproject.core.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.dao.PatientDao;
import org.motechproject.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * PatientDAO Integration Tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationCommon.xml" , "/persistenceIntegrationContext.xml"})
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
        patientDao.add(patient);

        Patient savedPatient = patientDao.get(patient.getId());
        assertEquals(patient, savedPatient);

        patientDao.remove(patient);

    }


    @Test
     public void testGetAllEmptyDB() throws Exception {

        List<Patient> patients = patientDao.getAll();
        assertEquals(0, patients.size());

    }

    //The following tests have been developed to research ektorp

    /*@Test
    //Method CouchDbRepositorySupport.getAll seems does not work
    //org.ektorp.DbAccessException: org.codehaus.jackson.map.exc.UnrecognizedPropertyException: Unrecognized field "dataObjectId" (Class org.motechproject.model.Patient), not marked as ignorable
    public void testGetAll() throws Exception {

        String id = "1_";

        Patient patient = new Patient();
        patient.setId(id);
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


}
