package org.motechproject.server.pillreminder.dao;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.domain.PillRegimen;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.pillreminder.util.TestUtil.newDate;

public class AllPillRegimensTest {

    @Mock
    CouchDbConnector couchDbConnector;
    private AllPillRegimens allPillRegimens;

    @Before
    public void setUp() {
        initMocks(this);
        allPillRegimens = new AllPillRegimens(couchDbConnector);
    }

    @Test
    public void shouldAddPillRegimen() {
        PillRegimen pillRegimen = new PillRegimen("123", newDate(2011, 1, 1), newDate(2011, 2, 1), 5, 10, null);
        allPillRegimens.add(pillRegimen);
        verify(couchDbConnector).create(pillRegimen);
    }


}
