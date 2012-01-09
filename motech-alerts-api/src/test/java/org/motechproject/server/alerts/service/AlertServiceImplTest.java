package org.motechproject.server.alerts.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.alerts.dao.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlertServiceImplTest {

    @Mock
    private AllAlerts allAlerts;
    @Mock
    private AlertFilter alertFilter;

    AlertService alertService;

    @Before
    public void setup() {
        initMocks(this);
        alertService = new AlertServiceImpl(allAlerts, alertFilter);
    }

    @Test
    public void shouldSearchForAlerts() {
        AlertCriteria criteria = new AlertCriteria().byStatus(AlertStatus.NEW).byType(AlertType.LOW).byExternalId("entity_id").byPriority(2);
        List<Alert> alerts = mock(List.class);
        when(alertFilter.search(criteria)).thenReturn(alerts);
        assertEquals(alerts, alertService.search(criteria));
    }
}
