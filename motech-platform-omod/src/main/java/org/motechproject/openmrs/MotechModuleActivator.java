package org.motechproject.openmrs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class logs the loading and unloading of the module in OpenMRS.
 */
public class MotechModuleActivator extends BaseModuleActivator {

    private Log log = LogFactory.getLog(this.getClass());

    @Override
    public void willStart() {
        super.willStart();
        log.info("Starting Motech Module");
    }

    @Override
    public void willStop() {
        super.willStop();
        log.info("Shutting down Motech Module");
    }

}
