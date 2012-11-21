package org.motechproject.openmrs.atomfeed.repository;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllAtomFeedUpdates extends MotechBaseRepository<AtomFeedUpdate> implements AtomFeedDao {
    private static final Logger LOGGER = Logger.getLogger(AllAtomFeedUpdates.class);

    @Autowired
    protected AllAtomFeedUpdates(@Qualifier("atomFeedCouchDbConnector") CouchDbConnector couchDbConnector) {
        super(AtomFeedUpdate.class, couchDbConnector);
    }

    @Override
    public void setLastUpdateTime(String id, String lastUpdateTime) {
        LOGGER.debug("Atom Feed Update: [id=" + id + ", lastUpdateTime=" + lastUpdateTime + "]");
        removeAll();
        add(new AtomFeedUpdate(lastUpdateTime, id));
    }

    @Override
    public String getLastUpdateTime() {
        List<AtomFeedUpdate> updates = getAll(1);
        if (updates.isEmpty()) {
            return null;
        }

        return updates.get(0).getLastUpdateTime();
    }

    @Override
    public String getLastId() {
        List<AtomFeedUpdate> updates = getAll(1);
        if (updates.isEmpty()) {
            return null;
        }

        return updates.get(0).getLastId();
    }

}
