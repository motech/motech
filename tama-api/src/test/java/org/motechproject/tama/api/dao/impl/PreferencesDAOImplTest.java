package org.motechproject.tama.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.event.EventRelay;
import org.motechproject.tama.api.model.Preferences;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PreferencesDAOImplTest
{
    @Mock
    EventRelay eventRelay;

    @Mock
    CouchDbConnector couchDbConnector;

    @InjectMocks
    PreferencesDAOImpl preferencesDAO;

    @Before
    public void setUp() {
        couchDbConnector = mock(CouchDbConnector.class);
        preferencesDAO = new PreferencesDAOImpl(couchDbConnector);
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testFindByClinicId() {
        Preferences p = preferencesDAO.findByClinicIdPatientId("cID", "pID");

        assertTrue(null == p);
    }
}
