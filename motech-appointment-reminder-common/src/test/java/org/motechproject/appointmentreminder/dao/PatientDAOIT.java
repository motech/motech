/**
 * 
 */
package org.motechproject.appointmentreminder.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.appointmentreminder.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * PatientDAO Integration Tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"/applicationCommon.xml", "/persistenceIntegrationContext.xml"})
public class PatientDAOIT {

    @Autowired
    private PatientDAO patientDao;

    private Patient patient;


}