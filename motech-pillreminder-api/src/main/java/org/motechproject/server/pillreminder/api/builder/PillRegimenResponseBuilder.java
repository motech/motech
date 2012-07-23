package org.motechproject.server.pillreminder.api.builder;

import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.api.domain.DailyScheduleDetails;
import org.motechproject.server.pillreminder.api.domain.Dosage;
import org.motechproject.server.pillreminder.api.domain.PillRegimen;

import java.util.ArrayList;
import java.util.List;

public class PillRegimenResponseBuilder {

    private DosageResponseBuilder dosageResponseBuilder = new DosageResponseBuilder();

    public PillRegimenResponse createFrom(PillRegimen pillRegimen) {
        List<DosageResponse> dosages = new ArrayList<DosageResponse>();
        for (Dosage dosage : pillRegimen.getDosages()) {
            dosages.add(dosageResponseBuilder.createFrom(dosage));
        }
        DailyScheduleDetails scheduleDetails = pillRegimen.getScheduleDetails();
        return new PillRegimenResponse(pillRegimen.getId(), pillRegimen.getExternalId(), scheduleDetails.getPillWindowInHours(), scheduleDetails.getRepeatIntervalInMinutes(), scheduleDetails.getBufferOverDosageTimeInMinutes(), dosages);
    }
}
