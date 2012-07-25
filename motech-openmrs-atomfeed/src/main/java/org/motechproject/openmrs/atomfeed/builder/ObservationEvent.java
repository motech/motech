package org.motechproject.openmrs.atomfeed.builder;

import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.openmrs.atomfeed.model.Entry;

public class ObservationEvent extends EventBuilderTemplate {

    public ObservationEvent(Entry entry) {
        super(entry);
    }

    @Override
    protected String getCreateAction() {
        return EventSubjects.OBSERVATION_CREATE;
    }

    @Override
    protected String getUpdateAction() {
        return EventSubjects.OBSERVATION_UPDATE;
    }

    @Override
    protected String getVoidedAction() {
        return EventSubjects.OBSERVATION_VOIDED;
    }

    @Override
    protected String getDeleteAction() {
        return EventSubjects.OBSERVATION_DELETED;
    }
}
