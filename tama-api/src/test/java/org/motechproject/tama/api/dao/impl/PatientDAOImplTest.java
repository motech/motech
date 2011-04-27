package org.motechproject.tama.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.event.EventRelay;
import org.motechproject.tama.api.model.Patient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PatientDAOImplTest
{
    @Mock
    EventRelay eventRelay;

    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    PatientDAOImpl patientDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        patientDAO = new PatientDAOImpl(couchDbConnector);
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testFindByClinicId() {
        Patient p = patientDAO.findByClinicIDPatientId("cID", "pID");

        assertTrue(null == p);
    }

    @Test
    public void testFindByClinicId_MultipleResults() {
        Patient p1 = new Patient();
        Patient p2 = new Patient();
        List<Patient> list = new ArrayList<Patient>();
        list.add(p1);
        list.add(p2);

        when(couchDbConnector.queryView(any(ViewQuery.class), Matchers.<Class<Patient>>any())).thenReturn(list);

        Patient p = patientDAO.findByClinicIDPatientId("cID", "pID");

        assertTrue(p1 == p);
    }
}
