package org.motechproject.openmrs;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.motechproject.openmrs.security.OpenMRSSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ResourceBundle;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationOpenmrsAPI.xml"})
public class OpenMRSIntegrationTestBase {
    @Autowired
    OpenMRSSession openMRSSession;

    @Before
    public void setUp() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("openmrs");
        OpenMRSTestAuthenticationProvider.login(resourceBundle.getString("openmrs.admin.username"), resourceBundle.getString("openmrs.admin.password"));
        openMRSSession.open();
        openMRSSession.authenticate();
    }

    @After
    public void tearDown() {
        openMRSSession.close();
    }


    protected void authorizeAndRollback(DirtyData dirtyData) {
        openMRSSession.open();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("openmrs");
        org.openmrs.api.context.Context.authenticate(resourceBundle.getString("openmrs.admin.username"), resourceBundle.getString("openmrs.admin.password"));
        dirtyData.rollback();
        openMRSSession.close();
    }

    protected interface DirtyData {
        void rollback();
    }
}
