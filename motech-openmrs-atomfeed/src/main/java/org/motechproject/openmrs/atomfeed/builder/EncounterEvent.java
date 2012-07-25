package org.motechproject.openmrs.atomfeed.builder;

import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.openmrs.atomfeed.model.Entry;

public class EncounterEvent extends EventBuilderTemplate {

    public EncounterEvent(Entry entry) {
        super(entry);
    }

    @Override
    protected String getCreateAction() {
        return EventSubjects.ENCOUNTER_CREATE;
    }

    @Override
    protected String getUpdateAction() {
        return EventSubjects.ENCOUNTER_UPDATE;
    }

    @Override
    protected String getVoidedAction() {
        return EventSubjects.ENCOUNTER_VOIDED;
    }

    @Override
    protected String getDeleteAction() {
        return EventSubjects.ENCOUNTER_DELETED;
    }

}
