package org.motechproject.tama.api.model;

/**
 * Created by IntelliJ IDEA.
 * User: rob
 * Date: 4/25/11
 * Time: 8:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppointmentSchedule
{
	public static enum Followup {
		REGISTERED("Registered",7),
		WEEK4("4 week follow-up",7*4),
		WEEK12("12 week follow-up",7*12),
		WEEK24("24 week follow-up",7*24),
		WEEK36("36 week follow-up",7*36),
		WEEK48("48 week follow-up",7*48);

		final String value;
		final int days;

		Followup(String value, int days) {
			this.value=value;
			this.days=days;
		}

		@Override
		public String toString(){
			return value;
		}

		public String getKey(){
			return name();
		}

		public int getDays() {
			return this.days;
		}
	}
}
