package org.motechproject.pillreminder.api.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.pillreminder.api.dao.PillReminderDao;
import org.motechproject.pillreminder.api.model.PillReminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class PillReminderCouchDBDaoImpl extends MotechAuditableRepository<PillReminder> implements PillReminderDao {

	@Autowired
	public PillReminderCouchDBDaoImpl( @Qualifier("pillReminderDatabase") CouchDbConnector db) {
		super(PillReminder.class, db);
		initStandardDesignDocument();
	}

	@Override
	@GenerateView
	public List<PillReminder> findByExternalId(String externalId) {
		return queryView("by_externalId", externalId);
	}

	@Override
	public List<PillReminder> findByExternalIdAndWithinWindow(String externalId, Date time){
		List<PillReminder> results = new ArrayList<PillReminder>();
		List<PillReminder> pillReminders = findByExternalId(externalId);
		if (pillReminders != null) {
			for (PillReminder pillReminder : pillReminders) {
				//'contains' is inclusive of the start instant and exclusive of the end, so we need to plus one day for the end
				Interval interval = new Interval(new DateTime(pillReminder.getStartDate()), new DateTime(pillReminder.getEndDate()).plusDays(1));
				if (interval.contains(new DateTime(time).withMillisOfDay(0))
						&& pillReminder.getScheduleWithinWindow(time) != null) {
					results.add(pillReminder);
				}
			}
		}
		return results;
	} 
}
