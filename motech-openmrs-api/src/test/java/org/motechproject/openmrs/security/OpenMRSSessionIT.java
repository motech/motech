package org.motechproject.openmrs.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationOpenmrsAPI.xml"})
public class OpenMRSSessionIT {

    @Autowired
    private OpenMRSSession openMRSSession;


    @Test(expected = APIException.class)
    public void shouldRequireOpeningASession() {
        openMRSSession.authenticate("admin", "P@ssw0rd");
    }

    @Test
    public void shouldAuthenticateUsingAnOpenSession() {
        openMRSSession.open();
        openMRSSession.authenticate("admin", "P@ssw0rd");
    }

    @Test(expected = APIException.class)
    public void shouldRequireOpeningASessionAgainAfterClosingIt() {
        openMRSSession.open();
        openMRSSession.authenticate("admin", "P@ssw0rd");
        openMRSSession.close();
        openMRSSession.authenticate("admin", "P@ssw0rd");
    }

}
