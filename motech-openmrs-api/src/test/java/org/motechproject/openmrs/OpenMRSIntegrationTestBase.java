package org.motechproject.openmrs;

import org.motechproject.openmrs.security.OpenMRSSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ResourceBundle;

public class OpenMRSIntegrationTestBase {
    @Autowired
    OpenMRSSession openMRSSession;

    public void setUp() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("openmrs");
        OpenMRSTestAuthenticationProvider.login(resourceBundle.getString("openmrs.admin.username"), resourceBundle.getString("openmrs.admin.password"));
        openMRSSession.open();
        openMRSSession.authenticate();
    }

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
