package org.motechproject.tama.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.event.EventRelay;
import org.motechproject.tama.api.model.Doctor;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DoctorDAOImplTest
{
    @Mock
    EventRelay eventRelay;

    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    DoctorDAOImpl doctorDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        doctorDAO = new DoctorDAOImpl(couchDbConnector);
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testFindByClinicId() {
        List<Doctor> list = doctorDAO.findByClinicId("cID");

        assertTrue(list.isEmpty());
    }
}
