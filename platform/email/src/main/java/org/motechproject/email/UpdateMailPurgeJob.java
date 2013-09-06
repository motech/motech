package org.motechproject.email;


import org.springframework.beans.factory.annotation.Autowired;

/**
 * The <code>UpdateMailPurgeJob</code> class is called at the server startup and updates mail purging settings.
 */

public class UpdateMailPurgeJob {

    @Autowired
    private InitializeSettings initializeSettings;

    @Autowired
    public void updateMailPurgeJob() {
        initializeSettings.handleSettingsChange();
    }

}
