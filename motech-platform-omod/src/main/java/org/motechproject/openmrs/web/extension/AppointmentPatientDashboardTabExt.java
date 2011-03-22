package org.motechproject.openmrs.web.extension;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.PatientDashboardTabExt;

public class AppointmentPatientDashboardTabExt extends PatientDashboardTabExt {

    /**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getMediaType()
     */
    @Override
    public Extension.MEDIA_TYPE getMediaType() {
        return Extension.MEDIA_TYPE.html;
    }

    /**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getTabName()
     */
    @Override
    public String getTabName() {
        return "motech.appointment";
    }

    /**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getRequiredPrivilege()
     */
    @Override
    public String getRequiredPrivilege() {
        return "";
        // return "Visit Scheduler - View Patient Dashboard";
    }

    /**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getTabId()
     */
    @Override
    public String getTabId() {
        return "appointment";
    }

    /**
     * @see org.openmrs.module.web.extension.PatientDashboardTabExt#getPortletUrl()
     */
    @Override
    public String getPortletUrl() {
        return "appointment.portlet";
    }
}