package org.motechproject.server.alerts.service;

import org.motechproject.server.alerts.dao.AllAlerts;
import org.springframework.beans.factory.annotation.Autowired;

public class AlertServiceImpl {
	AllAlerts allAlerts;
	@Autowired
	public AlertServiceImpl(AllAlerts allAlerts) {
		this.allAlerts = allAlerts;
	}
}
