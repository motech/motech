package org.motechproject.server.pillreminder.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechAuditableDataObject;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class ScheduleDetails {

   private int repeatInterval;

    protected ScheduleDetails(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public int getRepeatIntervalInMinutes() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}
