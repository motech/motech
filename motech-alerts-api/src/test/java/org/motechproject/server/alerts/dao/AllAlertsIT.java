package org.motechproject.server.alerts.dao;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/applicationContextAlert.xml"})
public class AllAlertsIT {

    @Autowired
    private AllAlerts allAlerts;

    private List<Alert> createdAlerts;

    private static int MAX_RECORDS = 100;

    @Before
    public void setUp() {
        createdAlerts = new ArrayList<Alert>();

        List<Alert> list = allAlerts.listAlerts(null, null, null, null, MAX_RECORDS);
        for (Alert alert : list) {
            allAlerts.remove(alert);
        }
    }

    @After
    public void tearDown() {
        for (Alert alert : createdAlerts)
            allAlerts.remove(allAlerts.get(alert.getId()));
    }

    @Test
    public void shouldReturnAllAlertsWithGivenExternalIdOnly() {
        createAlert("included_entity", AlertType.HIGH, AlertStatus.CLOSED, 3, null, DateUtil.now());
        createAlert("excluded_entity", AlertType.LOW, AlertStatus.CLOSED, 8, null, DateUtil.now());
        createAlert("included_entity", AlertType.LOW, AlertStatus.NEW, 2, null, DateUtil.now());

        Collection<Alert> alerts = allAlerts.findByExternalId("included_entity");
        assertEquals(Arrays.asList("included_entity", "included_entity"), extract(alerts, on(Alert.class).getExternalId()));
    }

    @Test
    public void shouldReturnAllAlertsWithGivenAlertTypeOnly() {
        createAlert("entity1", AlertType.HIGH, AlertStatus.CLOSED, 3, null, DateUtil.now());
        createAlert("entity2", AlertType.LOW, AlertStatus.CLOSED, 8, null, DateUtil.now());
        createAlert("entity3", AlertType.LOW, AlertStatus.NEW, 2, null, DateUtil.now());

        Collection<Alert> alerts = allAlerts.findByAlertType(AlertType.LOW);
        assertEquals(Arrays.asList(AlertType.LOW, AlertType.LOW), extract(alerts, on(Alert.class).getAlertType()));
    }

    @Test
    public void shouldReturnAllAlertsWithGivenAlertStatusOnly() {
        createAlert("entity1", AlertType.HIGH, AlertStatus.CLOSED, 3, null, DateUtil.now());
        createAlert("entity2", AlertType.LOW, AlertStatus.CLOSED, 8, null, DateUtil.now());
        createAlert("entity3", AlertType.LOW, AlertStatus.NEW, 2, null, DateUtil.now());

        Collection<Alert> alerts = allAlerts.findByStatus(AlertStatus.CLOSED);
        assertEquals(Arrays.asList(AlertStatus.CLOSED, AlertStatus.CLOSED), extract(alerts, on(Alert.class).getStatus()));
    }

    @Test
    public void shouldReturnAllAlertsWithGivenPriorityOnly() {
        createAlert("entity1", AlertType.HIGH, AlertStatus.CLOSED, 1, null, DateUtil.now());
        createAlert("entity2", AlertType.LOW, AlertStatus.CLOSED, 3, null, DateUtil.now());
        createAlert("entity3", AlertType.LOW, AlertStatus.NEW, 2, null, DateUtil.now());

        Collection<Alert> alerts = allAlerts.findByPriority(1);
        assertEquals(Arrays.asList(1), extract(alerts, on(Alert.class).getPriority()));
    }

    @Test
    public void shouldReturnAllAlertsWithoutAnyFilter() {
        createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts(null, null, null, null, MAX_RECORDS);
        assertEquals(2, listAlerts.size());
    }

    @Test
    public void shouldReturnMaxNumberOfAlertsWithoutAnyFilter() {
        createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts(null, null, null, null, 1);
        assertEquals(1, listAlerts.size());
    }

    @Test
    public void shouldFilterAlertsBasedOnExternalId() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts("222", null, null, null, MAX_RECORDS);
        assertEquals(1, listAlerts.size());
        assertEquals(alert2.getId(), listAlerts.get(0).getId());
    }

    @Test
    public void shouldFilterAlertsBasedOnAlertType() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts(null, AlertType.CRITICAL, null, null, MAX_RECORDS);
        assertEquals(1, listAlerts.size());
        assertEquals(alert2.getId(), listAlerts.get(0).getId());
    }

    @Test
    public void shouldFilterAlertsBasedOnAlertStatus() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts(null, null, AlertStatus.NEW, null, MAX_RECORDS);
        assertEquals(1, listAlerts.size());
        assertEquals(alert1.getId(), listAlerts.get(0).getId());
    }

    @Test
    public void shouldFilterAlertsBasedOnDateRange() {
        DateTime now = DateUtil.now();
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, now.minusDays(2));
        Alert alert2 = createAlert("112", AlertType.HIGH, AlertStatus.NEW, 2, null, now.minusDays(1));
        Alert alert3 = createAlert("113", AlertType.HIGH, AlertStatus.NEW, 2, null, now);
        Alert alert4 = createAlert("113", AlertType.HIGH, AlertStatus.NEW, 2, null, now.plusDays(1));

        List<Alert> listAlerts = allAlerts.findByDateTime(now.minusDays(1), now.plusDays(1));
        assertEquals(3, listAlerts.size());
        assertEquals(alert2.getId(), listAlerts.get(0).getId());
        assertEquals(alert3.getId(), listAlerts.get(1).getId());
        assertEquals(alert4.getId(), listAlerts.get(2).getId());
    }

    @Test
    public void shouldFilterAlertsBasedOnAlertPriority() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts(null, null, null, 1, MAX_RECORDS);
        assertEquals(1, listAlerts.size());
        assertEquals(alert2.getId(), listAlerts.get(0).getId());
    }

    @Test
    public void shouldFindAlertsWithOneOrMoreMatchingFilterCriteria() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());
        Alert alert3 = createAlert("111", AlertType.CRITICAL, AlertStatus.NEW, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts("111", AlertType.CRITICAL, null, null, MAX_RECORDS);
        assertEquals(1, listAlerts.size());
        assertEquals(alert3.getId(), listAlerts.get(0).getId());
    }

    @Test
    public void shouldFindAlertsWithoutAnyMatchingFilterCriteria() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());
        Alert alert3 = createAlert("111", AlertType.CRITICAL, AlertStatus.NEW, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts("333", AlertType.CRITICAL, null, null, MAX_RECORDS);
        assertEquals(0, listAlerts.size());
    }

    @Test
    public void shouldSortAlertsWhenNoFiltersAreSprecified() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts(null, null, null, null, MAX_RECORDS);
        assertEquals(2, listAlerts.size());
        assertEquals(alert2.getId(), listAlerts.get(0).getId());
        assertEquals(alert1.getId(), listAlerts.get(1).getId());
    }

    @Test
    public void shouldSortAlertsWhenFiltersAreSprecified() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());
        Alert alert3 = createAlert("111", AlertType.CRITICAL, AlertStatus.NEW, 1, null, DateUtil.now());

        List<Alert> listAlerts = allAlerts.listAlerts("111", null, AlertStatus.NEW, null, MAX_RECORDS);
        assertEquals(2, listAlerts.size());
        assertEquals(alert3.getId(), listAlerts.get(0).getId());
        assertEquals(alert1.getId(), listAlerts.get(1).getId());
    }

    @Test
    public void shouldNotChangeDateTimeWhenChangingTheStatus() {
        Alert alert1 = createAlert("111", AlertType.HIGH, AlertStatus.NEW, 2, null, DateUtil.now());
        Alert alert2 = createAlert("222", AlertType.CRITICAL, AlertStatus.CLOSED, 1, null, DateUtil.now());

        DateTime alert1DateTime = alert1.getDateTime();
        final Alert alert = allAlerts.get(alert1.getId());
        alert.setStatus(AlertStatus.CLOSED);
        allAlerts.update(alert);

        assertEquals(alert1DateTime, alert.getDateTime());
    }

    private Alert createAlert(String externalId, AlertType alertType, AlertStatus alertStatus, int priority, Map<String, String> data, DateTime dateTime) {
        Alert alert = new Alert(externalId, alertType, alertStatus, priority, data);
        alert.setDateTime(dateTime);
        allAlerts.add(alert);
        createdAlerts.add(alert);
        return alert;
    }
}
