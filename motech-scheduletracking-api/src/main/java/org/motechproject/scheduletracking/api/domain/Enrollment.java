package org.motechproject.scheduletracking.api.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;

import java.util.LinkedList;
import java.util.List;

@TypeDiscriminator("doc.type === 'Enrollment'")
public class Enrollment extends MotechBaseDataObject {
    @JsonProperty
    private LocalDate enrollmentDate;

    private String externalId;
    private List<MilestoneFulfillment> fulfillments = new LinkedList<MilestoneFulfillment>();
    private String scheduleName;
    private LocalDate referenceDate;
    private String currentMilestoneName;

    // For ektorp
    private Enrollment() {
    }

    public Enrollment(String externalId, String scheduleName, LocalDate enrollmentDate, LocalDate referenceDate, String currentMilestoneName) {
        this.externalId = externalId;
        this.scheduleName = scheduleName;
        this.enrollmentDate = enrollmentDate;
        this.referenceDate = referenceDate;
        this.currentMilestoneName = currentMilestoneName;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCurrentMilestoneName() {
        return currentMilestoneName;
    }

    public List<MilestoneFulfillment> getFulfillments() {
        return fulfillments;
    }

    public void fulfillMilestone(String nextMilestoneName, LocalDate dateFulfilled) {
        fulfillments.add(new MilestoneFulfillment(currentMilestoneName, dateFulfilled));
        currentMilestoneName = nextMilestoneName;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    @JsonIgnore
    public LocalDate getLastFulfilledDate() {
        LocalDate dateFulfilled = referenceDate;

        if (!fulfillments.isEmpty())
            dateFulfilled = fulfillments.get(fulfillments.size() - 1).getDateFulfilled();

        return dateFulfilled;
    }

    // For ektorp
    private String getType() {
        return type;
    }

    // For ektorp
    private void setCurrentMilestoneName(String currentMilestoneName) {
        this.currentMilestoneName = currentMilestoneName;
    }

    // For ektorp
    private void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    // For ektorop
    private void setType(String type) {
        this.type = type;
    }

    // For ektorp
    private void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    // For ektorp
    private void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }
}