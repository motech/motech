package org.motechproject.server.alerts.service;

import org.motechproject.server.alerts.dao.AllAlerts;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertCriteria.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

@Component
public class AlertFilter {

    private AllAlerts allAlerts;

    private interface FetchAlerts {
        List<Alert> search(AlertCriteria alertCriteria);
    }

    private interface FilterAlerts {
        List<Alert> search(List<Alert> alerts, AlertCriteria alertCriteria);
    }

    Map<Criterion, FetchAlerts> fetchByCriterion;
    Map<Criterion, FilterAlerts> filterByCriterion;

    @Autowired
    public AlertFilter(final AllAlerts allAlerts) {
        this.allAlerts = allAlerts;
        defineFetchFunctions();
        defineFilterFunctions();
    }

    public List<Alert> search(AlertCriteria alertCriteria) {
        List<Criterion> filters = alertCriteria.getFilters();
        if (CollectionUtils.isEmpty(filters))
            return allAlerts.getAll();
        Criterion primaryCriterion = filters.get(0);
        List<Alert> filtered = fetchByCriterion.get(primaryCriterion).search(alertCriteria);
        for (Criterion secondaryCriterion : filters.subList(1, filters.size()))
            filtered = filterByCriterion.get(secondaryCriterion).search(filtered, alertCriteria);
        return filtered;
    }

    private void defineFetchFunctions() {
        this.fetchByCriterion = new HashMap<Criterion, FetchAlerts>();
        this.fetchByCriterion.put(Criterion.externalId, new FetchAlerts() {
            public List<Alert> search(AlertCriteria criteria) {
                return allAlerts.findByExternalId(criteria.externalId());
            }
        });
        this.fetchByCriterion.put(Criterion.alertType, new FetchAlerts() {
            public List<Alert> search(AlertCriteria criteria) {
                return allAlerts.findByAlertType(criteria.alertType());
            }
        });
        this.fetchByCriterion.put(Criterion.alertStatus, new FetchAlerts() {
            public List<Alert> search(AlertCriteria criteria) {
                return allAlerts.findByStatus(criteria.alertStatus());
            }
        });
        this.fetchByCriterion.put(Criterion.alertPriority, new FetchAlerts() {
            public List<Alert> search(AlertCriteria criteria) {
                return allAlerts.findByPriority(criteria.alertPriority());
            }
        });
    }

    private void defineFilterFunctions() {
        this.filterByCriterion = new HashMap<Criterion, FilterAlerts>();
        this.filterByCriterion.put(Criterion.externalId, new FilterAlerts() {
            public List<Alert> search(List<Alert> alerts, AlertCriteria alertCriteria) {
                return filter(having(on(Alert.class).getExternalId(), equalTo(alertCriteria.externalId())), alerts);
            }
        });
        this.filterByCriterion.put(Criterion.alertType, new FilterAlerts() {
            public List<Alert> search(List<Alert> alerts, AlertCriteria alertCriteria) {
                return filter(having(on(Alert.class).getAlertType(), equalTo(alertCriteria.alertType())), alerts);
            }
        });
        this.filterByCriterion.put(Criterion.alertStatus, new FilterAlerts() {
            public List<Alert> search(List<Alert> alerts, AlertCriteria alertCriteria) {
                return filter(having(on(Alert.class).getStatus(), equalTo(alertCriteria.alertStatus())), alerts);
            }
        });
        this.filterByCriterion.put(Criterion.alertPriority, new FilterAlerts() {
            public List<Alert> search(List<Alert> alerts, AlertCriteria alertCriteria) {
                return filter(having(on(Alert.class).getPriority(), equalTo(alertCriteria.alertPriority())), alerts);
            }
        });
    }
}
