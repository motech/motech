package org.motechproject.model;

import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;

public class Time {

	private Integer hour;

	private Integer minute;
	
	public Time(){}

	public Time(Integer hour, Integer minute){
		this.hour = hour;
		this.minute = minute;
	}
	
	public Integer getHour() {
		return hour;
	}
	
	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getMinute() {
		return minute;
	}
	
	public void setMinute(Integer minute) {
		this.minute = minute;
	}

    @Deprecated
	@JsonIgnore
	public Date getTimeOfDate(Date date){
		if (hour != null && minute != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(DateUtils.truncate(date, Calendar.DATE));
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			return cal.getTime();
		} else {
			return null;
		}
	}

	@JsonIgnore
	public DateTime getDateTime(DateTime today){
        return new DateTime(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth(), hour, minute, 0, 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hour == null) ? 0 : hour.hashCode());
		result = prime * result + ((minute == null) ? 0 : minute.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Time other = (Time) obj;
		if (hour == null) {
			if (other.hour != null)
				return false;
		} else if (!hour.equals(other.hour))
			return false;
		if (minute == null) {
			if (other.minute != null)
				return false;
		} else if (!minute.equals(other.minute))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Time [hour=" + hour + ", minute=" + minute + "]";
	}
	
}
