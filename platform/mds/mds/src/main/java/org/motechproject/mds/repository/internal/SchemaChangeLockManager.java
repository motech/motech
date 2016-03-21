package org.motechproject.mds.repository.internal;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.domain.SchemaChangeLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

/**
 * Manager for obtaining the schema change lock.
 */
@Repository
public class SchemaChangeLockManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaChangeLockManager.class);

    public static final int LOCK_TIMEOUT_SECONDS = 5 * 60; // 5 minutes

    @Autowired
    private PersistenceManagerFactory persistenceManagerFactory;

    @Transactional(propagation = Propagation.MANDATORY)
    public void acquireLock(String comment) {
        LOGGER.info("Attempting to acquire lock for: {}", comment);

        final PersistenceManager pm = getPersistenceManager();

        final Boolean originalSerializedRead = pm.currentTransaction().getSerializeRead();
        try {
            pm.currentTransaction().setSerializeRead(true);

            final DateTime waitStartTime = DateUtil.now();

            SchemaChangeLock lock = null;
            while (lock == null && beforeTimeout(waitStartTime)) {
                // we keep retrying until timeout is reached
                try {
                    LOGGER.info("Waiting for lock...");
                    lock = getLock(pm);
                } catch (JDOException e) {
                    LOGGER.debug("Exception thrown while waiting for lock, probably timed out", e);
                }
            }

            if (lock == null) {
                throw new IllegalStateException("Unable to acquire schema change lock for: " + comment);
            }

            lock.setComment(comment);
            lock.setTimestamp(DateUtil.now());

            LOGGER.info("Lock acquired for: {}", comment);
        } finally {
            pm.currentTransaction().setSerializeRead(originalSerializedRead);
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void releaseLock(String comment) {
        LOGGER.info("Releasing lock for: {}", comment);

        final PersistenceManager pm = getPersistenceManager();

        final Boolean originalSerializedRead = pm.currentTransaction().getSerializeRead();
        try {
            pm.currentTransaction().setSerializeRead(true);

            SchemaChangeLock lock = getLock(pm);

            lock.setComment(null);
            lock.setTimestamp(null);

            LOGGER.info("Lock released for: {}", comment);
        } finally {
            pm.currentTransaction().setSerializeRead(originalSerializedRead);
        }
    }

    private SchemaChangeLock getLock(PersistenceManager pm) {
        Query query = pm.newQuery(SchemaChangeLock.class);
        query.setUnique(true);

        return (SchemaChangeLock) query.execute();
    }
    private boolean beforeTimeout(DateTime waitStartTime) {
        return DateUtil.now().isBefore(waitStartTime.plusSeconds(LOCK_TIMEOUT_SECONDS));
    }

    private PersistenceManager getPersistenceManager() {
        return persistenceManagerFactory.getPersistenceManager();
    }
}
