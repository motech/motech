package org.motechproject.server.pillreminder;


import org.apache.commons.collections.Predicate;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.util.PillReminderTime;
import org.springframework.beans.factory.annotation.Autowired;

import static org.apache.commons.collections.CollectionUtils.find;

public class ReminderEventHandler {

    private final OutboundEventGateway outboundEventGateway;

    private final AllPillRegimens allPillRegimens;

    private final PillReminderTime pillRegimenTime;

    @Autowired
    public ReminderEventHandler(OutboundEventGateway outboundEventGateway, AllPillRegimens allPillRegimens, PillReminderTime pillRegimenTime) {
        this.outboundEventGateway = outboundEventGateway;
        this.allPillRegimens = allPillRegimens;
        this.pillRegimenTime = pillRegimenTime;
    }

    @MotechListener(subjects = {EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER})
    public void handleEvent(MotechEvent motechEvent) {
        PillRegimen pillRegimen = getPillRegimen(motechEvent);
        Dosage dosage = getDosage(pillRegimen, motechEvent);
        final int pillWindow = pillRegimen.getReminderRepeatWindowInHours();

        if (shouldRaisePillReminderEvent(pillWindow, dosage)) {
            outboundEventGateway.sendEventMessage(createNewMotechEvent(dosage, pillRegimen, motechEvent));
        }
    }

    private MotechEvent createNewMotechEvent(Dosage dosage, PillRegimen pillRegimen, MotechEvent eventRaisedByScheduler) {
        MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT);
        motechEvent.getParameters().putAll(eventRaisedByScheduler.getParameters());
        int pillWindow = pillRegimen.getReminderRepeatWindowInHours();
        int retryInterval = pillRegimen.getReminderRepeatIntervalInMinutes();

        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TIMES_SENT, pillRegimenTime.timesPillRemindersSent(dosage, pillWindow, retryInterval));
        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TIMES_TO_BE_SENT, pillRegimenTime.timesPillRemainderWillBeSent(pillWindow, retryInterval));
        return motechEvent;
    }

    private PillRegimen getPillRegimen(MotechEvent motechEvent) {
        final String pillRegimenExternalId = (String) motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        return allPillRegimens.findByExternalId(pillRegimenExternalId);
    }

    private Dosage getDosage(PillRegimen pillRegimen, MotechEvent motechEvent) {
        final String dosageId = (String) motechEvent.getParameters().get(EventKeys.DOSAGE_ID_KEY);
        return findDosage(dosageId, pillRegimen);
    }

    private Dosage findDosage(final String dosageId, PillRegimen pillRegimen) {
        return (Dosage) find(pillRegimen.getDosages(), new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                Dosage dosage = (Dosage) o;
                return dosage.getId().equals(dosageId);
            }
        });
    }

    private boolean shouldRaisePillReminderEvent(int pillWindow, Dosage dosage) {
        return !pillRegimenTime.pillWindowExpired(dosage, pillWindow);
    }
}
