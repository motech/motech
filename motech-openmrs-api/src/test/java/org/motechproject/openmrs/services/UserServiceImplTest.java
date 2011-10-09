package org.motechproject.openmrs.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mrs.services.MRSException;
import org.openmrs.api.db.DAOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceImplTest {
    @Mock
    private org.openmrs.api.UserService userService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testChangeCurrentUserPassword() throws Exception {
        new UserServiceImpl(userService).changeCurrentUserPassword("p1", "p2");
        verify(userService).changePassword("p1","p2");
    }

    @Test(expected = MRSException.class)
    public void testChangeCurrentUserPasswordFailed() throws Exception {
        doThrow(mock(DAOException.class)).when(userService).changePassword("p1","p2");
        new UserServiceImpl(userService).changeCurrentUserPassword("p1", "p2");
    }
}
