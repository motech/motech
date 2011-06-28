package org.motechproject.server.pillreminder.dao;

import org.motechproject.dao.BaseDao;
import org.motechproject.server.pillreminder.domain.PillReminder;

import java.util.Date;
import java.util.List;

public interface PillReminderDao  extends BaseDao<PillReminder>{

	List<PillReminder> findByExternalId(String externalId);
	List<PillReminder> findByExternalIdAndWithinWindow(String externalId, Date time);
}
