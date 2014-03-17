package org.motechproject.mds.service;

import java.util.List;

/**
 * The <code>HistoryService</code> provides methods related with processing historical changes on
 * the given instance of entity.
 */
public interface HistoryService {

    /**
     * Records changes made on the given instance of entity. The first historical data should be
     * equal to data inside the given instance. Two instance of historical data should be
     * connected using appropriate fields (defined in history class definition).
     *
     * @param instance an instance created from the given entity definition.
     */
    void record(Object instance);

    /**
     * Removes all historical data with information what changes were made on the given instance
     * of entity.
     *
     * @param instance an instance created from the given entity definition.
     */
    void remove(Object instance);

    /**
     * Sets the trash flag for historical data related with the given instance object.
     *
     * @param instance an instance created from the given entity definition.
     * @param flag     true if instance was moved to trash; otherwise false.
     */
    void setTrashFlag(Object instance, Object trash, boolean flag);

    List getHistoryForInstance(Object instance);
}
