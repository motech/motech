package org.motechproject.scheduletracking.api.domain.json;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;

@TypeDiscriminator("doc.type === 'ScheduleRecord'")
public class ScheduleRecord extends MotechBaseDataObject {

    @JsonProperty
    private String name;
    @JsonProperty
    private boolean absolute;
    @JsonProperty
    private List<MilestoneRecord> milestones = new ArrayList<MilestoneRecord>();

    public String name() {
        return name;
    }

    public List<MilestoneRecord> milestoneRecords() {
        return milestones;
    }

    @JsonIgnore
    public boolean isAbsoluteSchedule() {
        return absolute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScheduleRecord that = (ScheduleRecord) o;

        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
