package org.motechproject.server.alerts.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.server.alerts.EventKeys;
import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.domain.UpdateCriterion;
import org.motechproject.server.alerts.repository.AllAlerts;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlertEventHandlerTest {


    public static final String EXTERNAL_ID = "externalId";
    public static final String ALERT_NAME = "alertName";
    public static final String ALERT_DESCRIPTION = "alertDescription";
    public static final int VALUE = 1;
    public static final String ALERT_ID = "123";

    @InjectMocks
    private AlertEventHandler alertEventHandler = new AlertEventHandler();

    @Mock
    private AllAlerts allAlerts;

    @Mock
    private AlertServiceImpl alertService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldCreateAlertFromEvent() {
        Map<String, Object> param = new HashMap<>();
        Map<String, String> alertData = new HashMap<>();
        alertData.put("data1", "data1");
        alertData.put("data2", "data2");
        param.put(EventKeys.EXTERNAL_ID_KEY, EXTERNAL_ID);
        param.put(EventKeys.ALERT_NAME, ALERT_NAME);
        param.put(EventKeys.ALERT_DESCRIPTION, ALERT_DESCRIPTION);
        param.put(EventKeys.ALERT_TYPE, AlertType.MEDIUM);
        param.put(EventKeys.ALERT_STATUS, AlertStatus.NEW);
        param.put(EventKeys.ALERT_PRIORITY, VALUE);
        param.put(EventKeys.ALERT_DATA, alertData);
        MotechEvent event = new MotechEvent(EventKeys.CREATE_ALERT_SUBJECT, param);
        alertEventHandler.create(event);
        verify(alertService).create(EXTERNAL_ID, ALERT_NAME, ALERT_DESCRIPTION, AlertType.MEDIUM, AlertStatus.NEW, VALUE, alertData);

    }

    @Test
    public void shouldChangeStatusForClose() {
        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.ALERT_ID, ALERT_ID);
        ArgumentCaptor<UpdateCriteria> captor = ArgumentCaptor.forClass(UpdateCriteria.class);
        MotechEvent event = new MotechEvent(EventKeys.CLOSE_ALERT_SUBJECT, param);
        alertEventHandler.updateStatus(event);
        verify(alertService).update(eq(ALERT_ID), captor.capture());
        Assert.assertEquals(AlertStatus.CLOSED, captor.getValue().getAll().get(UpdateCriterion.status));
    }

    @Test
    public void shouldChangeStatusForRead() {
        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.ALERT_ID, ALERT_ID);
        ArgumentCaptor<UpdateCriteria> captor = ArgumentCaptor.forClass(UpdateCriteria.class);
        MotechEvent event = new MotechEvent(EventKeys.MARK_ALERT_READ_SUBJECT, param);
        alertEventHandler.updateStatus(event);
        verify(alertService).update(eq(ALERT_ID), captor.capture());
        Assert.assertEquals(AlertStatus.READ, captor.getValue().getAll().get(UpdateCriterion.status));
    }
}
