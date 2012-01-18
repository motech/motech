package org.motechproject.scheduletracking.api.domain.enrollment;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.domain.Schedule;

import java.util.LinkedList;
import java.util.List;

@TypeDiscriminator("doc.type === 'Enrollment'")
public class Enrollment extends MotechBaseDataObject {
	private static final long serialVersionUID = 5097894298798275204L;

	private String externalId;
    private LocalDate enrolledDate;
    private String scheduleName;
    private List<MilestoneFulfillment> fulfillments = new LinkedList<MilestoneFulfillment>();
    private String nextMilestone;

    private Enrollment() {
    }

    public Enrollment(String externalId, LocalDate enrolledDate, String scheduleName, String firstMilestone) {
        this.externalId = externalId;
        this.enrolledDate = enrolledDate;
        this.scheduleName = scheduleName;
        this.nextMilestone = firstMilestone;
    }

    public List<Alert> getAlerts(Schedule schedule) {
        LocalDate dateLastFulfilled = getEnrolledDate();

        if (!fulfillments.isEmpty()) {
            MilestoneFulfillment fulfillment = fulfillments.get(fulfillments.size() - 1);
            dateLastFulfilled = fulfillment.getDateFulfilled();
        }

        return schedule.alertsFor(dateLastFulfilled, nextMilestone);
    }

    public String fulfillMilestone(Schedule schedule) {
        return fulfillMilestone(schedule, LocalDate.now());
    }

    public String fulfillMilestone(Schedule schedule, LocalDate fulfilledOn) {
        fulfillments.add(new MilestoneFulfillment(nextMilestone, fulfilledOn));
        return nextMilestone = schedule.nextMilestone(nextMilestone);
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setNextMilestone(String nextMilestone) {
        this.nextMilestone = nextMilestone;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public LocalDate getEnrolledDate() {
        return enrolledDate;
    }

    public void setEnrolledDate(LocalDate enrolledDate) {
        this.enrolledDate = enrolledDate;
    }

    public String getNextMilestone() {
        return nextMilestone;
    }

    public List<MilestoneFulfillment> getFulfillments() {
        return fulfillments;
    }
}
