package org.motechproject.server.alerts.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.alerts.EventKeys;
import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AlertEventHandler {

    @Autowired
    private AlertServiceImpl alertService;

    @MotechListener(subjects = {EventKeys.CREATE_ALERT_SUBJECT})
    public void create(MotechEvent event) {
        String externalId = event.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        String alertName = event.getParameters().get(EventKeys.ALERT_NAME).toString();
        String alertDescription = event.getParameters().get(EventKeys.ALERT_DESCRIPTION).toString();
        AlertType alertType =AlertType.valueOf(event.getParameters().get(EventKeys.ALERT_TYPE).toString().toUpperCase());
        AlertStatus alertStatus = AlertStatus.valueOf(event.getParameters().get(EventKeys.ALERT_STATUS).toString().toUpperCase());
        Integer alertPriority = (Integer)event.getParameters().get(EventKeys.ALERT_PRIORITY);
        Map alertData = (Map)event.getParameters().get(EventKeys.ALERT_DATA);
        alertService.create(externalId, alertName, alertDescription, alertType, alertStatus, alertPriority.intValue(), alertData);
    }

    @MotechListener(subjects = {EventKeys.CLOSE_ALERT_SUBJECT, EventKeys.MARK_ALERT_READ_SUBJECT})
    public void updateStatus(MotechEvent event) {
        String id = event.getParameters().get(EventKeys.ALERT_ID).toString();
        UpdateCriteria criteria = new UpdateCriteria();
        if (event.getSubject().equals(EventKeys.CLOSE_ALERT_SUBJECT)) {
            criteria.status(AlertStatus.CLOSED);
        } else {
            criteria.status(AlertStatus.READ);
        }
        alertService.update(id, criteria);
    }
}
