package org.motechproject.pillreminder.api.dao;

import java.util.Date;
import java.util.List;

import org.motechproject.dao.BaseDao;
import org.motechproject.pillreminder.api.model.PillReminder;

public interface PillReminderDao  extends BaseDao<PillReminder>{

	List<PillReminder> findByExternalId(String externalId);
	List<PillReminder> findByExternalIdAndWithinWindow(String externalId, Date time);
}
