package org.motechproject.server.pillreminder;


import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.util.PillReminderTime;
import org.springframework.beans.factory.annotation.Autowired;

public class ReminderEventHandler {

    @Autowired
    private OutboundEventGateway outboundEventGateway;

    @Autowired
    private AllPillRegimens allPillRegimens;

    private PillReminderTime pillReminderTime;

    public ReminderEventHandler() {
        pillReminderTime = new PillReminderTime();
    }

    public ReminderEventHandler(OutboundEventGateway outboundEventGateway, AllPillRegimens allPillRegimens, PillReminderTime pillRegimenTime) {
        this.outboundEventGateway = outboundEventGateway;
        this.allPillRegimens = allPillRegimens;
        this.pillReminderTime = pillRegimenTime;
    }

    @MotechListener(subjects = {EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT_SCHEDULER})
    public void handleEvent(MotechEvent motechEvent) {
        PillRegimen pillRegimen = getPillRegimen(motechEvent);
        Dosage dosage = getDosage(pillRegimen, motechEvent);
        final int pillWindow = pillRegimen.getReminderRepeatWindowInHours();

        if (shouldRaiseEvent(dosage, pillWindow)) {
            outboundEventGateway.sendEventMessage(createNewMotechEvent(dosage, pillRegimen, motechEvent));
        }
    }

    private boolean shouldRaiseEvent(Dosage dosage, int pillWindow) {
        return !(pillReminderTime.isDosageTaken(dosage, pillWindow));
    }

    private MotechEvent createNewMotechEvent(Dosage dosage, PillRegimen pillRegimen, MotechEvent eventRaisedByScheduler) {
        MotechEvent motechEvent = new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT);
        motechEvent.getParameters().putAll(eventRaisedByScheduler.getParameters());
        int pillWindow = pillRegimen.getReminderRepeatWindowInHours();
        int retryInterval = pillRegimen.getReminderRepeatIntervalInMinutes();

        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TIMES_SENT, pillReminderTime.timesPillRemindersSent(dosage, pillWindow, retryInterval));
        motechEvent.getParameters().put(EventKeys.PILLREMINDER_TOTAL_TIMES_TO_SEND, pillReminderTime.timesPillRemainderWillBeSent(pillWindow, retryInterval));
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
        for(Dosage dosage: pillRegimen.getDosages()) {
              if(dosage.getId().equals(dosageId)) return dosage;
        }
        return null;
    }
}
