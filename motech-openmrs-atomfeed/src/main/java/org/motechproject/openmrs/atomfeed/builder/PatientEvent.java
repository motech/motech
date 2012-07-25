package org.motechproject.openmrs.atomfeed.builder;

import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.openmrs.atomfeed.model.Entry;

public class PatientEvent extends EventBuilderTemplate {

    public PatientEvent(Entry entry) {
        super(entry);
    }

    @Override
    protected String getCreateAction() {
        return EventSubjects.PATIENT_CREATE;
    }

    @Override
    protected String getUpdateAction() {
        return EventSubjects.PATIENT_UPDATE;
    }

    @Override
    protected String getVoidedAction() {
        return EventSubjects.PATIENT_VOIDED;
    }

    @Override
    protected String getDeleteAction() {
        return EventSubjects.PATIENT_DELETED;
    }
}
