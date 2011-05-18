package org.motechproject.pillreminder.api.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.motechproject.model.MotechAuditableDataObject;

@TypeDiscriminator("doc.type === 'PillReminder'")
public class PillReminder extends MotechAuditableDataObject {

	private static final long serialVersionUID = 1L;

	private String externalId;
	private Date startDate;
	private Date endDate;
	
	private List<Schedule> schedules = new ArrayList<Schedule>();
	private List<Medicine> medicines = new ArrayList<Medicine>();
	
	@JsonProperty("type") private final String type = "PillReminder";

	public List<Medicine> getMedicines() {
		return medicines;
	}

	public void setMedicines(List<Medicine> medicines) {
		this.medicines = medicines;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<Schedule> getSchedules() {
		return schedules;
	}
	
	@JsonIgnore
	public Schedule getScheduleWithinWindow(Date time) {
		if (schedules != null) {
			for (Schedule schedule : schedules) {
				Interval interval = new Interval(new DateTime(schedule.getWindowStart().getTimeOfDate(time)), new DateTime(schedule.getWindowEnd().getTimeOfDate(time)));
				if(interval.contains(new DateTime(DateUtils.truncate(time, Calendar.MINUTE)))){
					return schedule;
				}
			}
		}
		return null;
	}

	public void setSchedules(List<Schedule> schedules) {
		this.schedules = schedules;
	}

	@Override
	public String toString() {
		return "PillReminder [externalId=" + externalId + ", startDate="
				+ startDate + ", endDate=" + endDate + ", schedules="
				+ schedules + ", medicines=" + medicines + ", type=" + type
				+ "]";
	}

}
