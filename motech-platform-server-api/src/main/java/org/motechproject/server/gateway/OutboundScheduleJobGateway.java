package org.motechproject.server.gateway;

import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;

public interface OutboundScheduleJobGateway {
	
	/**
	 * Schedule a recurring job
	 * @param job Job details object
	 */
	public void scheduleRecurringJob(SchedulableJob job);
	
	/**
	 * Schedule a non-recurring job
	 * @param job Job details object
	 */
	public void scheduleOneTimeJob(RunOnceSchedulableJob job);
}
