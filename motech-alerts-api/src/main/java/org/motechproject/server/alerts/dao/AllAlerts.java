package org.motechproject.server.alerts.dao;

import java.util.ArrayList;
import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AllAlerts extends MotechBaseRepository<Alert> {

	@Autowired
	public AllAlerts(@Qualifier("alertDbConnector") CouchDbConnector db) {
		super(Alert.class, db);
		initStandardDesignDocument();
	}
	
	@View(name = "findByExternalId", map = "function(doc) {if (doc.type == 'Alert') {emit(doc.externalId, doc._id);}}")
	private List<Alert> findByExternalId(String externalId) {
		ViewQuery q = createQuery("findByExternalId").key(externalId).includeDocs(true);
        List<Alert> alerts = db.queryView(q, Alert.class);
        return alerts.isEmpty() ? null : alerts;
	}
	
	@View(name = "findByAlertType", map = "function(doc) {if (doc.type == 'Alert') {emit(doc.alertType, doc._id);}}")
	private List<Alert> findByAlertType(AlertType alertType) {
		ViewQuery q = createQuery("findByAlertType").key(alertType).includeDocs(true);
        List<Alert> alerts = db.queryView(q, Alert.class);
        return alerts.isEmpty() ? null : alerts;
	}
	
	@View(name = "findByAlertStatus", map = "function(doc) {if (doc.type == 'Alert') {emit(doc.status, doc._id);}}")
	private List<Alert> findByAlertStatus(AlertStatus alertStatus) {
		ViewQuery q = createQuery("findByAlertStatus").key(alertStatus).includeDocs(true);
        List<Alert> alerts = db.queryView(q, Alert.class);
        return alerts.isEmpty() ? null : alerts;
	}
	
	@View(name = "findByAlertPriority", map = "function(doc) {if (doc.type == 'Alert') {emit(doc.priority, doc._id);}}")
	private List<Alert> findByAlertPriority(int priority) {
		ViewQuery q = createQuery("findByAlertPriority").key(priority).includeDocs(true);
        List<Alert> alerts = db.queryView(q, Alert.class);
        return alerts.isEmpty() ? null : alerts;
	}	
	
	public List<Alert> listAlerts(String externalId, AlertType alertType, AlertStatus alertStatus, Integer alertPriority) {
		List<Alert> alerts = new ArrayList<Alert>();
		
		if (externalId != null) {
			alerts = findByExternalId(externalId);
		}
		
		if (alertType != null) {
			alerts.retainAll(findByAlertType(alertType));
		}
		
		if (alertStatus != null) {
			alerts.retainAll(findByAlertStatus(alertStatus));
		}
		
		if (alertPriority != null) {
			alerts.retainAll(findByAlertPriority(alertPriority));
		}
		
		return alerts;
	}


//	@View(name = "listAlerts", map = "function(doc) {if (doc.type == 'Alert') {emit([doc.externalId, doc.alertType, doc.status, doc.priority], doc._id);}}")
//	public List<Alert> listAlerts(String externalId, AlertType alertType, AlertStatus status, Integer priority, int max) {
//		ComplexKey key = ComplexKey.of( (externalId==null? ComplexKey.emptyObject():externalId),
//										(alertType == null? ComplexKey.emptyObject(): alertType),
//										(status == null? ComplexKey.emptyObject(): status),
//										(priority == null? ComplexKey.emptyObject(): priority));
//		ViewQuery q = createQuery("listAlerts").key(key).includeDocs(true);
//		
//        List<Alert> alerts = db.queryView(q, Alert.class);
//        return alerts.isEmpty() ? null : alerts;
//	}
}